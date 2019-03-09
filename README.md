# dynodao

```
taras@me:~$ dynodao
 ________________________________________
/ An annotation processor for generating \
| fluent, type-safe DynamoDB             |
\ interactions.                          /
 ----------------------------------------
\                             .       .
 \                           / `.   .' " 
  \                  .---.  <    > <    >  .---.
   \                 |    \  \ - ~ ~ - /  /    |
         _____          ..-~             ~-..-~
        |     |   \~~~\.'                    `./~~~/
       ---------   \__/                        \__/
      .'  O    \     /               /       \  " 
     (_____,    `._.'               |         }  \/~~~/
      `----.          /       }     |        /    \__/
            `-.      |       /      |       /      `. ,~~|
                ~-.__|      /_ - ~ ^|      /- _      `..-'   
                     |     /        |     /     ~-.     `-. _  _  _
                     |_____|        |_____|         ~ - . _ _ _ _ _>
```

Puns aside, the dinosaur wearing a top hat is spot on. Working with the DynamoDb Java SDK can tedious. The addition of DynamoDBMapper removes much of the tedium by using magic to serialize and deserialize classes, but [working with expressions](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBMapper.QueryScanExample.html) is still awkward, and all that magic (read: reflection) incurs a runtime cost.

Dynodao takes a lot of inspiration from DynamoDBMapper. Compared to it, dynodao offers:

* **Fast execution** by using plain methods instead of reflective access
* **Compile-time type safety** where possible
  * If an object cannot be mapped to an `AttributeValue`, you get a compilation error
  * The generated classes are specific to a *schema* which only contain operations allowed on the defined types
* **Easily debuggable code** as there is no magic at runtime
* **Real lazy pagination** as `PaginatedList` end up [storing all results](https://github.com/puppetlabs/aws-sdk-for-java/blob/master/src/main/java/com/amazonaws/services/dynamodb/datamodeling/PaginatedList.java#L121) from a read operation in memory

To generate all of the classes, you must define a *schema* class. This is just a POJO (aka java-bean) which corresponds to the data you'd store in Dynamo. This class must adhear to the [java-beans](https://en.wikipedia.org/wiki/JavaBeans) specification, ie needs a default contructor, and getters and setters.

```java
@DynoDaoSchema(tableName = "things")
class MySchema {
    @DynoDaoHashKey
    private String hashKey;
    
    @DynoDaoRangeKey
    private String rangeKey;

    // default ctor, getters, setters omitted...
}
```

This then generates a bunch of classes in the same package as your schema class. You don't really need to care about most of them (ctrl+space is your friend!). The main ones are:
* `MySchemaStagedDynamoBuilder`
  * Your main entry point to the suite of classes.
  * Has `using` methods to specify an index or table to operate on. The returns have `with` methods to specify keys.
* `MySchemaAttributeValueSerializer`
  * A utility classes for serializing each type in `MySchema` to and from `AttributeValue`. You shouldn't need to use it explicitly, it's just nice to know about in case you need it.

To make a query you use the staged builder entry point and specify the index to use and then the keys. Pass that object to `DynoDao#get` which will make the appropriate read operation for you. This results a class with a lazy stream which automatically performs the pagination for you, keeping only one page in memory at a time.
```java
DynoDao dynoDao = new DynoDao(amazonDynamoDbInstance);
Stream<MySchema> queryResult = dynoDao.get(new MySchemaStagedDynamoBuilder()
        .usingIndexName()
        .withHashKey("hashKey")
        .withRangeKey("rangeKey"));
```

```java
// -- the above is the same as --
QueryRequest request = new QueryResult()
        .withTable("things")
        .withIndexName("index-name")
        .withKeyConditionExpression(":hashKey = #hashKey AND :rangeKey = #rangeKey")
        .addExpressionAttributeNamesEntry(":hashKey", "hashKey")
        .addExpressionAttributeNamesEntry(":rangeKey", "rangeKey")
        .addExpressionAttributeValuesEntry("#hashKey", new AttributeValue().withS("hashKey"))
        .addExpressionAttributeValuesEntry("#rangeKey", new AttributeValue().withS("rangeKey"));
do {
    QueryResult result = amazonDynamoDb.query(request);
    for (Map<String, AttributeValue> item : result.getItems()) {
        MySchema mySchema = new MySchema();
        mySchema.setHashKey(item.get("hashKey").getS());
        mySchema.setRangeKey(item.get("rangeKey").getS());
        // do things with mySchema
    }
    request.setExclusiveStartKey(result.getLastEvaluatedKey());
} while (request.getExclusiveStartKey() != null);
```

```java
// -- or, using DynamoDBMapper (though it stores all results in memory) --
DynamoDBQueryExpression<MySchema> query = new DynamoDBQueryExpression<>()
        .withIndexName("index-name")
        .withKeyConditionExpression(":hashKey = #hashKey AND :rangeKey = #rangeKey")
        .addExpressionAttributeNamesEntry(":hashKey", "hashKey")
        .addExpressionAttributeNamesEntry(":rangeKey", "rangeKey")
        .addExpressionAttributeValuesEntry("#hashKey", new AttributeValue().withS("hashKey"))
        .addExpressionAttributeValuesEntry("#rangeKey", new AttributeValue().withS("rangeKey"));
List<MySchema> results = dynamoDbMapper.query(MySchema.class, query);
```

# Notes

## Additional Parameters to Query (etc)
One option to implement other (or all!) query attributes is to use decorators like the following:

```java
public TableHashKeyModel withReturnCapacity() {  
    return new TableHashKeyModel(/*hashKey, true*/) {
        @Override
        protected QueryRequest asQueryRequest() {
            QueryRequest query = super.asQueryRequest();
            query.setReturnConsumedCapacity("true");
            return query;
        }
    };
}
```
That is, the class itself is stateless but chains state together through perpetually overriding `asQueryRequest()`. This can even be done for `withHashKey` and the like.

Though, `equals()` is funky then. We could implement equals as `buildQuery().equals(o.buildQuery())` instead.

Another option is to store the request object in each stage, then the withers clone the request and mutate the corresponding field. Or, perhaps more simply, store each relevent field in each stage.

## Parallel Scan
Scan and ParallelScan are both done using `ScanRequest`. How can we specify the number of segments seamlessly? Ideally, we can hide the parallel stuff inside of the spliterator, where each split has it's own segment to scan. It possible to choose how many times a spliterator splits by evaluating the stream in it's own `ForkJoinPool`. We'd have to ensure the stream cannot be made sequential and that each split has the proper `ScanRequest` segment provided.
