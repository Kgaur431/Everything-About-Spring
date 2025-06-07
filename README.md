# All about Spring.

### Available web servers with Spring boot
    A web server is a system that can statisfy a client request on the public internet.  
    Tomcat, Jetty
    {groupId means where the dependency come from,}
    How to change Tomcat to Jetty. 
        Go to pom.xml file, under the spring-boot-starter-web dependecy (it brings the tomcat server by default)
        <exclusions> <groupId>org.springframework.boot</groupId> <artificatId>spring-boot-starter-tomcat</artificat> </exclusions> 
        now to add the jetty, Go to dependency section.
            <dependencies> 
                <dependency> <groupId>org.springframework.boot</groupId> <artificatId>spring-boot-starter-jetty</artificat>
            </dependencies>
        then reload the project. by right click on the pom.xml file and go to build and then reload. 
        
### N-tier Architecture                    
    N-tier architecture (also called multi-tier architecture) is about layering your application by responsibility,          {Splits app by logic responsibility: controller → service → repo}
        Think of this as 3 layers,
            [Controller Layer]   --> Handles HTTP/API
            [Service Layer]      --> Handles business logic
            [Repository Layer]   --> Talks to the database
        These are called tiers or layers. Your app is split into these layers based on responsibility, NOT by feature.  (Organizes files by the Layer called Layer-based package structure)

    feature-based package structure         {it's way of Organizes files by domain feature: like order/, user/, etc.}
        com.example.order
            ├── OrderController.java        (it's an Naming Convension)
            ├── OrderService.java
            └── OrderRepository.java
        Here, everything related to the "Order" feature is together — more modular and clean for large projects.
        but under the hood we still follow the N-tier architecture if: Controller talks to Service --> Service talks to Repository --> Repository talks to DB.

### Dependency Injection
    let say we have OrderController, OrderService, OrderRepository, Order classes, they are not linked with each other.
    now let say OrderController talking to OrderService {so we have to instansititate the OrderService obj in the OrderController class } and OrderService talking to OrderRepository and so on. 

        Bad Practice:-
            instansititate the object using new keyword inside the constructor. 
                here we have problem where we want to test a OrderService class becoz as soon as we instansititate OrderService class then a real instance get created and let say the same OrderService
                required in any other class then we again have to instansititate. basically it is an bad code.  So we do Dependency Injection.
        Good Practice:-
            we will take the OrderService obj as a Parameterised constructor. 
                public OrderController (OrderService orderService){ this.orderService = orderService;}  this is the way of injecting the dependecy of OrderService class into OrderController class, 
                but before that we have to instansititate the OrderService class object. to do that with the help of dependecy injection by using the annotation like  on the OrderService class.
                and we have to annotate the constructor of OrderController class with @Autowired. (this is optional)
                @Component annotation will create a Bean and that bean we can inject into multiple places. and the cool thing is that the OrderService is an Singleton. so if we inject it in multiple classes then 
                it, we are getting the same instance. 

    Switch between multiple Dependencies {becoz of the interface}
        OrderService class talking to OrderRepository:-             
            we have OrderRepository class, and OrderFakeRepository class {Implementation classes}, also we have OrderRepo interface.
            Eg:-
                public interface OrderRepo{ List<Order> getOrders(); }

                public class OrderFakeRepository implements OrderRepo{
                    List<Order> getOrders() {return new Arrays.asList(new Order("pizza"), new Order("Dosa"))'}
                }

                public class OrderRepository implements OrderRepo{
                     List<Order> getOrders() { /* TODO:- Later we will connect with the database. */  return Collections.emptyList();}
                }

            Now we want to inject the dependecy of OrderRepo (interface) in the OrderService class. 
            Eg:-
                @Component
                public class OrderService {
                    private final OrderRepo orderRepo;      // interface
                    public OrderService (OrderRepo orderRepo) {
                        this.orderRepo = orderRepo
                    }
                    List<Order> getOrders(){    return orderRepo.getOrders(); }
                }

                // TODO:- Right now it have an issue, "Becoz we don't know which implements to inject this (OrderRepo orderRepo) means we have to choose between two Implementations.
                what we will do:-
                   // right now we want this class to available as Bean so we annotate it with
                   @Component(value = "fake")
                    public class OrderFakeRepository implements OrderRepo{
                        List<Order> getOrders() {return new Arrays.asList(new Order("pizza"), new Order("Dosa"))'}
                    }
                    @Component
                    public class OrderRepository implements OrderRepo{
                        List<Order> getOrders() { /* TODO:- Later we will connect with the database. */  return Collections.emptyList();}
                    }

                    this (OrderRepo orderRepo) knows which Implementation it have to pick up. Now the things are connected but next we will learn how to switch the Implementation
                        OrderService class is same as above.
            
            Next:-  (now the error should gone)
                it (OrderRepo orderRepo) throwing an error that "could not autowired there is more thn one bean of OrderRepo".
                what we have to do?
                    we have to annotate this (OrderRepo orderRepo) with (@Qualifier("fake")OrderRepo orderRepo)
                    so this name "fake" corresponds to the annotation which we have given at the OrderFakeRepository.
                    we are injecting the OrderFakeRepository instead of the OrderRepository.
                    as we have not implemented the OrderRepository. let say we add the @Primary annotation to the OrderRepository. 

                    Component
                    public class OrderService {
                        private final OrderRepo orderRepo;      // interface
                        public OrderService (@Qualifier("fake")OrderRepo orderRepo) {
                            this.orderRepo = orderRepo
                        }
                        List<Order> getOrders(){    return orderRepo.getOrders(); }
                    }

                    @Component
                    @Primary 
                    public class OrderRepository implements OrderRepo{
                        List<Order> getOrders() { /* TODO:- Later we will connect with the database. */  return Collections.emptyList();}
                    }

            Next:-  we Switch the Implementations,
                we have added the (@Qualifier("fake")OrderRepo orderRepo) in the OrderService class constructor, that we have to remove and add the @Autowired annotation at the OrderService class constructor.
                    it still optional but we are just adding. 
                    Component
                    public class OrderService {
                        private final OrderRepo orderRepo;      // interface
                        @Autowired
                        public OrderService (OrderRepo orderRepo) {
                            this.orderRepo = orderRepo
                        }
                        List<Order> getOrders(){    return orderRepo.getOrders(); }
                    }

                    the OrderRepository would remain same with @Primary annotation.

### @Component 
    what it does ?
        when we annotate any class (OrderService) with @Component then spring create a new instance of the class that we have annotated.
        when we instansititate these beans they have scopes. so the default scopes when we annotate it with this (OrderService) class then the spring create an new instance of this (OrderService) class
            and the scope is Singleton. which means if we inject it into another class then we are getting the same instance.   
            behind the scene spring uses the new keyword to create an instance but everything is managed by the spring. so we don't have to worry about lifespan of the instance.
        we can have different types of scopes like Prototypes, Request, Session. that we are not using as we are using the Singleton.      
        @Component annotation is the superset of all of the beans, means as we follow the N tier architecture therefor we have multiple layer so we can have more specific annotation like @Service, @Controller etc.
            @Component and @Service, @Controller etc. are exactlly same thing. but by using @Service it recognise that class is more specific. like that class is meant by the using as service.        

### Custom Bean OR @Bean annotation
    @Bean annotation allows us to instansititate a class where we can have some extra configuration and setup steps before the class is initialised.
    Eg:-   let say we have OrderConfiguration class. 
        @configuration
        public class OrderConfiguration {
            // TODO:- this class will do some kind of setup when the application starts. and we want to inject some services or classess that we have like Database connection,

            // DB connection, and to rest to tell that spring has to initialise this Below DB connection we can use @Bean which means below method has to instansititated for us and any code which is present inside the method 
                that should be exectue. 

                @Bean
                DBConnection getConnection () {retunr null;}
        }       this is one of the reason to use Bean

    Another example of using bean   
        let say we have to Implementations of OrderRepo and if we want to switch the Implementation by using some configuration. means How we can instansititate the OrderRepo by using some configuration.
        there the @Bean helps us.
             @configuration
             public class OrderConfiguration {
                @value("${app.useFakeOrderRepo:false}")  // it means if the app.useFakeOrderRepo value is not provided then we can use default value which is false.
                private Boolean useFakeOrderRepo;

                @Bean
                DBConnection getConnection () {retunr null;}

                @Bean
                OrderRepo orderRepo() { // we are trying to acheive a flexiblity of when to instansititate OrderFakeRepository or OrderRepository.

                    // first we create the value like useFakeOrderRepo; that we can get from the configuration file.

                    return useFakeOrderRepo ? new OrderFakeRepository() : new OrderRepository();

                    // now we don't have to use the Qualifier in the constructor anymore becoz this method will create the instance depending on the configuration. 
                    // and make sure that we have to remove the @Primary from the OrderRepository. becoz everything is done from the configuration.

                }
        }  

         @Component 
        public class OrderRepository implements OrderRepo{
            List<Order> getOrders() { /* TODO:- Later we will connect with the database. */  return Collections.emptyList();}
        }  

        as of now we have not provided the value so the app.useFakeOrderRepo = false, so it will instansititate the object of OrderRepository at runtime. 
        but let say we want to switch the Implementation then there are multiple ways to do it. 
            like :- we open the Edit configuration and here we have to pass the Program arguments like --app.useFakeOrderRepo=true and then click on apply and ok. 
            make sure the name app.useFakeOrderRepo should amtch with the name which we have given in the value section of the configuration file.
        now we switch the Implementation by having the Repository bean configured before its bean initialised.

        make sure that we have to remove the @Repository and @Repository(value="fake") from the OrderRepository and OrderFakeRepository becoz now we are instansititate the bean using configuration, 
        so if we put these annotation then the duplication of bean going on.

        weahter we have some kind of setup of configure before the initialization this is how we can do it.

### How spring does json serialization and deserialization   
        spring uses the Jackson Library.
        it is best JSON parser for java.

        let say we have Order class which have id and name properties and we have the getters like getId(), getName().
            let say if we remove the getters from the Order class and then run the app then we get the Internal Server Error, status=500.
                InvalidDefinationException: No serializer found for class com.kartik.demo.order.Order and no properties discovered to create BeanSerializer/
            basically spring sending back the properites to the client (like deserialization) is through these getters. means the properties are coming from the getters while deserialization.
            means if we don't remove the getter and also add one more getters like,
                public String getOrderId(){         // we have added this to understand the deserialization happening based on the getters which we defined. 
                    return id;
                }
                 public String getId(){
                    return id;
                }
                 public String getName(){
                    return name;
                }

                now we get the result as 
                {
                    "orderId": "1",             // we return the same id. and the naming Convension happend like this (remove the get and first letter small like order and then other are capital like Id)
                    "id": "1",
                    "name": "pizza"

                }

            we are retunring the list from the controller but it automatically converted into json object.
        
        let say we have password field in the Order POJO and we don't want to send the exact password back to the client.
        with Jackson we have one annotation called @JsonIgnore property, that we will add into the password field. 
            then the password will not send to the client. means it will be ignore while doing the deserialization.
        
        let say we have field called id and we want to change the name from id to orderId then we have one annotation in Jackson called @JsonPr  operty("orderId) that we have to put on the id field.
            now the client will see the orderId to id. 

        Getters are important. 

### HTTP method
    GET:-
        the http Get method request a representaton of the specified resource.
        Request using GET should only be used to request data.  (they shouldn't include data). 
    POST:-
        the http POST method send data to the server. 
        the type of the body of the request is indicated by the content-type header.  
    PUT:-
        it is mainly used for updating the resources. 
        the http PUT method creates also a new resource or replaces a representation of the target resource with the request payload. 
    DELETE:-
        the http DELETE method deletes the specified resource. 
         
    PUT V/S POST
        PUT should be idempotent, it means that if we want to create a new order then we can send the exact same request to the server and that should not have any side effects.  
            like if we send the same request 1 million times via PUT method then it does not have any side effect where as if we send it via POST method then it have 1 million sideeffect.   

    In @PostMapping, 
        here the client sends the Order order (it is an class) but we want to take the data as json object so the Client is send the json payload and the way that we reterived that by @ReqauestBody.

        @PostMapping 
        void createNewOrder (@ReqauestBody Order order){
            System.out.println("new order received" + order)
        }

        let say we want to send password, 
            we pass the password in the payload then we get the Internal server error. 
            exact error:- InvalidDefinationException No fallback setter/field defined for creator property 'password'.
            it happens becoz we have a getter method called password where we annotate it with the @JsonIgnore
                like:-
                    @JsonIgnore
                    public String getPassword() {return password;}

            to fix this issue we have to annotate the password field with @JsonProperty(access = Json.Property.Access.WRITE_ONLY)
                like:-
                    @JsonProperty(access = Json.Property.Access.WRITE_ONLY)
                    private final String password;

                this annotation will alow us to send a password from the client but not read a password from the client.

    In @PutMapping,
        it is doing the same thing that @PostMapping doing. but it updating the data.


    In @DeleteMapping,
        here this method takes a particular id and this id we will get from the @PathVariable.

        @DeleteMapping(path = "{id}")
        void deleteOrder(@PathVariable("id) Long id ){}



### Api Versioning
    writing HostName:PortNumber is a bed design. instead we have to follow a Naming Convension.
    like:-
        api/v1/order
            it means we are serving an api to our client, we should do versioning to our api, then name of the model.
            we can have multiple versions of our api.
            eg:-
                api/v3/order
                api/v2/order
                api/v4/order
    when we build the new version then we can't delete the older version of our api. becoz,
        let say we have a client like windows app & ios app. 
            assume windows app consuming the api versrion1 & ios app consuming the api version1. 
            now due the app is on windows it get mostly updated so this app now consuming the version 2.
            let say the ios user has not updated the app so it still using version1. and if we remove the version1 then our app will break this is not good for the business. 
            so we have to maintain all the version becoz we don't know which version has consumed by which version.  
    
    To do this versioning,
        use @RequestMapping(path = "api/v1/order") annotation and we define the path of our api. 
        optional:- we can update the @GetMapping(value = "all"). and all others are remain same.

    To create the version2 of our api,
        Duplcate the OrderController class with the suffix V2. like OrderControllerV2 class.
        and update the @RequestMapping(path = "api/v2/order") 

        the best practice is to depricate the older version of our api.
        like:-
            In OrderController class,
             @RequestMapping(path = "api/v1/order")
             @RestController 
             @Deprecated
             public class OrderController {
                ...
             }

### What is client sends the partial data. like api required name, id, password and client send only id then also the api get successfull. 
    so we have to enforce the clients to send the required data.

    How do we enforce that server side
        there are multiple ways of doing this:-
        1. we can write the logic in the OrderService class using Stream, filter and so on. 
        2. we can use the library which comes from springboot called springboot-starter-validation.
            add this dependency into the pom.xml file
            like:-
                <dependecy>
                <groupId>org.springframework.boot</groupId>
                <artificatId> spring-boot-starter-validation</artificatId>
                </dependecy>
            
            this validation library allows us to do basically enforce the constraints on our Models.
                like:-
                    name & password would always be mandatory so to do that. 
                    In the Order class we have these fields so we annotate these fields with the annotations. 

                        @NotBlank
                        private final String name;
                        @NotBlank
                        priavate final String password;

                    @NotNull v/s @NotBlank
                        NotNull means a property should not be Not null but it can be empty.
                        NotBlank means a properity can contain some character.
                            this annotation takes the message as well. 
                            like:-
                                @NotBlank(message = "name can not be empty")
                                these messages has not visible by the client therfore we have to add the exceptions to show this message to the client.
                                basically client doen't know what is happening?
                
                as of now we have implemented the annotations but we have not activated them.
                to activate these we have to go where the object or request payload are recieved like the controller class.    
                     @PostMapping 
                    void createNewOrder (@ReqauestBody @Valid Order order){
                        System.out.println("new order received" + order)
                    } 
                
            these annotations like @NotBlank, @NotNull are only work when we use the @Valid annotation at the @ReqauestBody. 

            let say we have an email field and we want to do the email validation. 
            @Email
            private final String email;

            assume that client send the email like kartik@deebeecom --> there might be a chance that it work. 
            so we can do is inside @Email() we can pass the regular expression for our email.

    to know more about these validation go to import statement and click on constraints like import javax.validation.constraints.Email;
        

### Handling exceptions 
        In springboot there are multiple ways of handle the exceptions.
            usually when we want to deal with client error or internal server error than we want to be more specific. like we have to send a message to the client.
            eg:- let say we thrown the exception from the service class but that exception message will not be show into the error message.
                code:-
                    Customer getCustomer(Long id){
                        return getCustomers().Stream().filter(customer -> customer.getId().equals(id)).findFirst().orElseThrow(() -> new CustomerNotFoundException(message: "customer with id " + id + " not found"));                        
                    }
        one way to handle exception,
            By customising the properties file. like application.properties file.
                server.error.include-message=always.
                server.error.include-binding-erros=always
                server.error.include-stacktrace=always.   // right now we are in developement phase, so when we are in production phase then we have to set this value as never.
            
            now we will get the message and trace in the error.

            let say we have set that value server.error.include-stacktrace=on_trace_param. 
                then we will not get the trace in error message.
                to get the trace in the error message we have to do this changes into the api call:-
                    localhost:8080/api/v1/customers/3
                    
                    localhost:8080/api/v1/customers/3?trace=true
            
            scanerio:-
                assume client send a request for customer id is 3 like localhost:8080/api/v1/customers/3, then we get the Internal Server Error as 500 but it is not an server error, its an Client error 
                becoz client request for an id which is not exists. 
                so "Status Code does not reflect the exact error", never blindly beleive on it.

                Server Error happens when something bad happen with our code. 

### HTTP Status Code
    Link:- https://developer.mozilla.org/en-US/docs/Web/HTTP/Status  read this docs. 


### Creating Custom Exception
    create a package called exception.
    create a class basec on the exception like NotFoundException etc. these classes will extend the RuntimeException.
        {runtime exception which can be thrown during the normal operation of the jvm, }
    we annotate this class with @ResponseStatus(HttpStatus.NOT_FOUND) :- it allow us to change the status code.
        eg:-
  
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public class NotFoundException extends RuntimeException{
                // TODO:- Creating a constructor which takes a message that we will pass to the super(). becoz when we use this class then we pass the message. 
                public NotFoundException(@NotNull String message) {
                    super(message);
                }
        }

        now the service class use this NotFoundException class.
         code:-
            Customer getCustomer(Long id){
                return getCustomers().Stream().filter(customer -> customer.getId().equals(id)).findFirst().orElseThrow(() -> new NotFoundException(message: "customer with id " + id + " not found"));                        
            }
    this is the best practice.
    we can also the customise the object which clients received.

        Object:-
            {
                "timestamp": "***",
                "status": 404,
                "error": "Not Found",
                "path": "/api/v1/customers/3",
                "message": "customer with id 3 not found"
            }

### Customization Error Object
    1. create a new class called ApiRequestException which extends RuntimeException.        // this is class which ww will use to throw an exception.
        public class ApiRequestException extends RuntimeException{
            public ApiRequestException(@NotNull String message) {
                super(message);
            }
            public ApiRequestException(@NotNull String message, Throwable cause) { // this is an actual cause of the error which we throw.
                super(message, cause);
            }
        }
    2. create a new class which will be an actual exception that client will see called ApiException.
        public class ApiException {
            private final ZonedDateTime timestamp;
            private final HttpStatus status;
            private final Throwable throwable;
            private final String message;

            public ApiException(ZonedDateTime timestamp, HttpStatus status, Throwable throwable, String message) {
                this.timestamp = timestamp;
                this.status = status;
                this.message = message;
            }
            // create the getter and toString method.    
        }

    3.  create a class where it will handle the Exception so we called ExceptionHandler.   // here we can handle multiple exception.
            we will use the ResponseEntity of tyep object to build the response for the client.  so instead of object we will pass our custom object which we have created like ApiException. 
        public class ExceptionHandler{
        public ResponseEntity<Object> handleApiRequestException (ApiException apiException){
                                        // () inside the ResponseEntity we can have HttpStatus status, we have the body of the Object. we can have both and so on. 
                return new ResponseEntity<>(apiException,HttpStatus.BAD_REQUEST);   // we are passing the apiException and then some status code.   
            }
        }

    rewatch lesson35.  (Not understood completely)

### ExceptionHandler & ControllerAdvice
     leacture 36 & leacture 37. (Not watched)


### H2 Database
        it is an relational database.   
        it is very lite database.
        we can configure it as In-Memory database.    

        H2 is a powerful and flexible relational database management system written in Java.
        It can operate in several modes:-
        In-Memory Mode: In this mode, all data is stored in the computer's main memory (RAM). This makes the database extremely fast, as it avoids the slower speed of disk I/O. 
                This is ideal for temporary data, caching, and running automated tests where you need a clean database for each test run.  
        Persistent (On-Disk) Mode: H2 can also store data in files on a disk, just like traditional databases such as MySQL or PostgreSQL.

        Operating Modes:-
            Embedded Mode: The database engine runs within the same Java Virtual Machine (JVM) as the application. 
                This is a very lightweight and easy-to-configure setup, as there's no need to run a separate database server.
            
            Server Mode: H2 can run as a separate server process. This allows multiple applications, potentially running on different machines, to connect to the same database.

    To install H2:-
        add the dependecy in the pom.xml file   
            <dependecy>
                <groupId> com.h2database</groupId>
                <artificatId> h2</artificatId>
               <scope> runtime</scope>
            </dependecy>
        then reload the project by right clicking on the pom.xml file and then go to build and then reload.
    
    add some configuration in the application.properites file   
        // these are specific to spring datasource.
        spring.datasource.url=jdbc:h2:mem:testdb    // mem means that it is an In-Memory database (we can configure it to persist data in file system),  name of the database.
        spring.datasource.driverClassName = org.h2.Driver // if we use postgresql or sql then it is an different driver.
        spring.datasource.username=kartik
        spring.datasource.password=

        // these are specific to spring data jpa.
        spring.jpa.database-platform= org.hibernate.dialect.H2Dialect
        spring.jpa.show.sql=true                // this means like it display the commands (like select, insert etc) that hibernate will execute while running the app. so we can see it in the console.

        // these are specific to h2 to enabled the console.
        spring.h2.console.enabled=true
        spring.h2.console.path=/h2

    Now we add the JPA.             // it is not mandatory that we add the jpa later. 
        add the dependecy in the pom.xml file
            <dependecy>
                <groupId> org.springframework.boot</groupId>
                <artificat> spring-boot-starter-data-jpa</artificat>
            </dependecy>    
        then reload the project by right clicking on the pom.xml file and then go to build and then reload.
    
    access the H2 console
        localhost:8080/h2 
            if the jdbc url is not present then paste it:- jdbc:h2:mem:testdb
                                                username:-kartik
                                                password:- empty
    
    Now we create the tables in the database based on our model.
        inside resources package:-
            create a file called schema.sql and data.sql   
            schema.sql file contain:-
                create table order (
                    id bigint generated by default as identity,
                    name varchar(255),
                    email varchar(255),
                    password varchar(255),
                    primary key (id)
                )
            
            data.sql file contain:-  //
                insert into order (id, name, email, password) values (101, 'kartik', 'kartik@kartik.com', 'kartik@123')
                ...
                ...
                etc. 
                

        restart the app and refresh the dattabase as well.

    now we can change the configuration from the Edit configuration section :- --app.useFakeOrderRepo=false             // if we have multiple Implementations.
        @Entity
        @Table
        public class Order{
            @Id
            private final Long id;
            ...
            }

        now the OrderRepository no more implements the orderRepo,

            public interface OrderRepository extends JpaRepository<Order, Long> {           // now it is an interface. <Order> becoz we work with Order class and Long becoz it is an type of the id.
            }
        
        now In OrderService class,
            OrderRepo is no more. becoz we use the db Implementations.
            so use @OrderRepository instead of OrderRepo.
            update the field name and constructor as well.

            public class OrderService {
                private final OrderRepository orderRepository;      // interface
                public OrderService (OrderRepository orderRepository) {
                    this.orderRepository = orderRepository
                }
                List<Order> getOrders(){    return orderRepository.findAll(); }
                Order getOrder(Long id){   return orderRepository.findById(id).orElseThrow(() -> new NotFoundException(message: "order with id " + id + " not found"));}
            }
        
        update the OrderRepo bean if we have multiple implementations and remove the OrderRepository. means orderRepo is return only the OrderFakeRepository.

        if the data is not picking up then try to execute the insert command in the h2 database. 
        also make some changes in Order class like:-
            always create a default constructor. 
            now the fields are no more final.
             
    Now as we are fetching the data from database, 
        we annotate the Order class with @Entity, @Table. these annotations allow us to map the Order class to a table in our database.
    leacture 40 & 41.

    make sure on every running the app or restart the app we have to execute all the sql staements becoz it's an in-memory database.


### Feign  
    it is nothing but an Http client.
    it ability to have an declaractive REST client for our apps.
    basically it makes a supereasy for us to create REST Client.

    url:- github.com/OpenFeign/feign
    url:- jsonplaceholder.typicode.com          // provides fake data for testing.

    what we will do?
        we are trying to use Springboot and Feign to consume the data from the jsonplaceholder.typicode.com.

    How we will do?
        we will build an client that will invoke Http request to these api's. like jsonplaceholder.typicode.com/todos, /post, /comments, /users etc. 
    
    this task is an related to Spring cloud.

    url:- spring.io/projects/spring-cloud-openfeign 

       SpringMVC annotations:-
        @GetMapping
        @PostMapping
        @PutMapping
        @DeleteMapping
        @PathVariable
        @RequestBody etc. 
    
    first we have to install the spring cloud dependency.
        there are so many sub projects in the spring cloud, 

        these we got from the spring.io/projects/spring-cloud-openfeign:- 

        <properites>
        <spring.cloud-version> Hoxton.SR8 </spring.cloud-version>
        </properites>

        <depencyManagement>
        <dependencies>
        <dependecy>
                <groupId> org.springframework.cloud</groupId>
                <artificatId> spring-cloud-dependencies</artificat>         // means we can pick any dependecy from the spring cloud.
                <version> ${spring-cloud.version}</version>
                <type>pom</type>
                <scope> import</scope>
        </dependecy>
        </dependencies>
        </depencyManagement>


        <dependencies>
            <dependecy>
                <groupId> org.springframework.cloud</groupId>
                <artificatId> spring-cloud-starter-openfeign</artificat>         // means we can pick any dependecy from the spring cloud.
                <version> ${spring-cloud.version}</version>
            </dependecy>
        </dependencies>
         

        let say we are wroking on spring 2.4 version of springboot and we have to "make sure that we are using the compatible version of the cloud dependecy (any dependecy) with our spring boot version".
        to do this,
            release train springboot compatiblity:-
            Release train                   Boot version
            2020.0.0 aka ilford     -->     2.4.0 
            Hoxton                  -->     2.2.x, 2.3, (starting with SR5)
            etc. 

        now as we are working on spring 2.4 version and right now we have Hoxton.SR8 version and both are not compatible with each other so we have to replace the cloud version which compatible with our 
        spring version. 

        <properites>
        <spring.cloud-version> 2020.0.0 </spring.cloud-version>
        </properites>
        

        then reload the project by right clicking on the pom.xml file and then go to build and then reload.

    leacture 44.

    In Main class,
        we have to annotate it with @EnableFeignClients
        next we will create a client which will be reterive the data from the jsonplaceholder. 
            @SpringBootApplication
            @EnableFeignClients
            public class Main {
                public static void main(String[] args) {
                    SpringApplication.run(Main.class, args);
                }
            }

        create the client which will fetch /posts.
            1.  create a new package called vendor_name. like  jsonplaceholder. 
            2.  to create a client we need to create an interface called vendor_nameClient. like JSONPlaceHolderClient.
                2.1 annotate it with a @FeignClient. 
                2.2 here we specify few things, like value="vendor_name", url="vendor_url".
            3. to fetch the data we simply create the MVC method like GET, POST etc in the JSONPlaceHolderClient.
                3.1 to fetch the data first we have to create the model class based on the data which we want to fetch.
                {as per the data which we get from the url, we read like we are getting the list of object of type Post}      
            
<!-- Suggested code may be subject to a license. Learn more: ~LicenseLog:121489239. -->
                package com.kartik.demo.jsonplaceholder;

                import org.springframework.cloud.openfeign.FeignClient;
                import org.springframework.web.bind.annotation.GetMapping;

                @FeignClient (value = "jsonplaceholder", url = "https://jsonplaceholder.typicode.com/")         // make sure we put the /forward slash at the end of the url. becoz we 
                public interface JSONPlaceHolderClient {

                    @GetMapping("posts")
                    List<Post> getPosts();      // like we are getting the list of Post so we have to create the Post Class.

                    @GetMapping("posts/{postId}")
                    Post getPost(@PathVariable ("postId") Integer postId);
                }

                public class Post{              // make sure the fields name are same. 
                    private Integer userId;
                    private Integer id;
                    private String title;
                    private String body;
                    
                    // create the constructor & parameterised constructor. 
                    // create the getter.
                    // create the toString().
                }

                q&A :- Why this (JSONPlaceHolderClient) is an interface. 
        
            4. To test our Client which we have to create JSONPlaceholderConfiguration class.       // we need this class so that it can execute our client.
                4.1 annotate this class with @configuration.
                4.2 we create the CommandLineRunner called runner ("here we inject the client") & return the args (call the endpoint which we have created);
                    4.2.1 annotate this runner with @Bean("jsonplaceholder")  
                
                @configuration
                public class JSONPlaceHolderClient {

                    CommandLineRunner runner(JSONPlaceHolderClientJ jsonPlaceHolder){
                        return args -> 
                        System.out.println(jsonPlaceHolder.getPosts.size());
                    }

                }

                In the console we will get the result like 100, size of the list of post. becoz of sout.

        OpenFeign is very useful as a REST client when the two microservices are communicate.
        we cam learn more about this from the spring cloud documentation.
    
### logging
    scanerio:- let say we build the web app and something goes wrong, we have deployed the application on AWS ans our service is serving the real traffic to real user. in this situation we don't know where to 
               start, this is where logging come into picture.
               we have to put the enogh logs so that we can troubleshoot the bugs.

    url:- https://docs.spring.io/spring-boot/docs/2.4.2/reference/htmlsingle/#boot-features-logging

    there are multiple frameworks which provide logging like Log4J, SLF4J, Logback.
    Springboot by default uses the Logback framework for logging.

    Get the instance for Logging:-

        Logger is an interface. due to which we can easily switch the implementation.
        it provides multiple methods like info(), error(), debug() speciic for debugging purpose, trace(), warn() for warning purpose etc.   
        import from SLF4J. 
        private final static Logger logger = LoggerFactory.getLogger("here we want to pass the class where we want to use the logger" OrderService.class);
            this is how we use the looger in our classes. 
            make sure the instance is static.  

        uses:-

            List<Order> getOrder() {
                logger.info("getCustomers was called");
                return orderRepository.findAll();
            }

            Customer getCustomer(Long id){
                return orderRepository.findById(id).orElseThrow(
                    () -> {
                        NotFoundException notFoundException =  new NotFoundException(message: "customer with id " + id + " not found"));
                       //  logger.error("error is getting customer {}" + id);       // nessage.         {} it represent the id.
                        logger.error("error is getting customer{}" + id + notFoundException.toString()); // we can pass the throwable as well. 
                        return notFoundException;
                    }
                }  
            
        don't add logging where the code is not adding the value. 
        it use to monitoring the application,
    
    Configure the banner & logging :-
      we want to switch the banner so open the application.properites file.
         spring.main.banner-mode=off 
     we can configure the logging:-
        we want to save the log into file:- logging.file.name()
                                            logging.file.path()
                                            logging.level()
                                            logging.rollingpolicy.file-name-pattern         // it will help the log file not to get bigger.
                                            etc.  



            


               




            




        
    

        



