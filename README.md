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
            
            Next:-
                it (OrderRepo orderRepo) throwing an error that "could not autowired there is more thn one bean of OrderRepo".
                what we have to do?
                    we have to annotate this (OrderRepo orderRepo) with (@Qualifier("fake")OrderRepo orderRepo)
                    so this name "fake" corresponds to the annotation which we have given at the OrderFakeRepository.



