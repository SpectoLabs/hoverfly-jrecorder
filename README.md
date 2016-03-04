

**java-hoverfly-exporter**
-----------------


**Quick start**
------------------

Simply add the filter to your tests (MockMvc in this example) `hoverflyFilter`:

```java
private static HoverflyFilter hoverflyFilter = new HoverflyFilter("www.my-test.com", "generated/hoverfly.json");

@Before
public void setUp() {
    this.mockMvc = webAppContextSetup(webApplicationContext)
            .addFilter(hoverflyFilter)
            .build();
}
```

The filter takes the baseUrl which you wish your virtualized version of this service to have, and the directory you want the `.json` file to be output to.  When you run the tests you will get a json output containing the recordings in a format of:

```java
[{
"request": {
    "path": "/api/bookings",
    "method": "POST",
    "destination": "www.my-test.com",
    "query": null,
    "body": ""
},
"response": {
    "status": 400,
    "body": "",
    "headers": {}
}]
```

You can then import the filter into hoverfly, for example using the hoverfly junit rule in your api consumer unit tests.