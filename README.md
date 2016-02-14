

**java-hoverfly-exporter**
-----------------

This a very quickly put together proof of concept for easy production of virtualized versions of your java services, otherwise known as API simulations.  It works by attaching a servlet filter to your tests which will record them and the output the results to a json file, which can then be imported into the open source service virtualization tool [Hoverfly](https://github.com/SpectoLabs/hoverfly)

I've realised that when testing and developing microservices, it can be hard to avoid a slow feedback cycle because failures aren't found until the multi-service test level.  This is because when testing in isolation:

 - If you rely on mocks there's little reliability in your tests as the very thing your server is supposed to do (orchestrate http calls with other services) is not being tested.  
 - Although using a stub external http server means you will test the correct thing, then there is too much room for manual error in your stubbing of the external services api contracts.
 - Existing api recording proxies tend to be a bit too cumbersome, and actually require you to spin up a web server to record them.  In our CI builds we like using mockMvc for very reason that it only simulates real http.

**Quick start**
------------------

This is a quickly put together POC which should be easy to set up.  In the example project we have a mockMvc project where we have added the `hoverflyFilter`:

    private static HoverflyFilter hoverflyFilter = new HoverflyFilter("www.my-test.com", "generated/hoverfly.json");

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .addFilter(hoverflyFilter)
                .build();
    }

The filter takes the baseUrl which you wish your virtualized version of this service to have, and the directory you want the `.json` file to be output to.  When you run the tests you will get a json output containing the recordings in a format of:

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

The idea is that every time you build your application an artefact would be produced containing recordings of all the interactions made by the tests.  As long as you write good tests,  you will have the majority of scenarios available for anyone who wants to test against a simulation of this service.  You will also be guaranteeing the simulation will comply with the api contract, and that any client written in any language will be able to use it.

Once you have the file you can export it into hoverfly:

`curl --data @path-to-file.json http://localhost:8888/records` 

Then you can try it out:

`curl 'http://www.my-test.com/api/flights?origin=Berlin&destination=Munich' --proxy http://localhost:8500/`

And start writing reliable tests.  Some future ideas would be:

 - Spinning up and tearing down hoverfly during your tests as part of the java ecosystem.
 - Producing hoverfly `.json` from other input sources, such as swagger api documentation.

