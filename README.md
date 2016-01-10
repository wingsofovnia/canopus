<p align="center">
   <img src="https://drive.google.com/uc?id=0B54N87Pu1jNTSG9SU0ItX3BZM00&authuser=0&export=download">
   <br/>
   <img src="https://travis-ci.org/wingsofovnia/canopus.svg?branch=develop">
</p>
**Canopus** is a library for service oriented applications, that provides service registration, discovery, loadbalancing and IPC (Inter Process Communication).

It is inspired by Netflix Ribbon, Netflix Eureka and Netflix Hystrix but promises to be light and easy to extend. It has built-in Circuit Breaker, Load Balancers and adapters for different Discovery services (Consul, Netflix Eureka (planned)) and HTTP Client, based on Netflix Feign. Thanks to its modular structure, you can use each of it's functionalities independently.

## Core modules
- **canopus-commons** - utils and shared models
- **canopus-discovery** - contains core API for service discovering
- **canopus-loadbalancing** - provides API and implementations of Load Balancers
- **canopus-exchanging** - supplies library with IPC wrapper of Netflix Feign and Circuit Breaking pattern


## Service Discovering
Service Discovering is represented by two main interfaces: ```ServiceDiscoverer``` for retrieving information about registered services, and ```ServiceRegistrator``` for service registration, which are located in **canopus-discovery** module. Particular implementations can be found in **canopus-discovery-*** modules (e.g. ```ConsulServiceDiscoverer``` and ```ConsulServiceRegistrator``` from **canopus-discovery-consul** module).

To register service, you need to create ```Service``` and register it withing ```ServiceRegistrator```:
```java
ServiceRegistrator serviceRegistrator = new ConsulServiceRegistrator("127.0.0.1");

Service myUserService = new Service("myUserService", 8080); // Private IP auto-detecting, service id auto-generating
serviceRegistrator.register(myUserService);

Service myWebService = new Service("10.20.30.40", 80, "myWebService", Service.Protocol.HTTPS);
serviceRegistrator.register(myWebService);

Service myMonitoredTestService = new Service("myTestService#id22", "myTestService", "10.40.21.11", 123, Service.Protocol.HTTPS, 20, 4);// Heartbeat interval = 20, timeout = 4
serviceRegistrator.register(myMonitoredTestService);
```

To retrieve information about registered services, ```ServiceDiscoverer``` is used:
```java
ServiceDiscoverer serviceDiscoverer = new ConsulServiceDiscoverer("127.0.0.1");

List<RemoteService> allRegisteredServices = serviceDiscoverer.resolve();
List<RemoteService> testServiceInstances = serviceDiscoverer.resolve("myTestServiceName");
```

## Load Balancing
Implementations of ```LoadBalancer``` interface can select most suitable ```RemoteService``` instance from list according to certain algorithm. For example, ```RandomLoadBalancer``` randomly balances load between services in the list.
```java
ServiceDiscoverer serviceDiscoverer = new ConsulServiceDiscoverer("127.0.0.1");
List<RemoteService> testServiceInstances = serviceDiscoverer.resolve("myTestServiceName");

LoadBalancer loadBalancer = new RandomLoadBalancer();
RemoteService loadBalancedServiceChoice = loadBalancer.choose(testServiceInstances);
```

## Inter Process Communication
```Interaction``` from **canopus-exchanging** allows to build remote service client by interface. This functionality is provided by [Netflix Feign](https://github.com/Netflix/feign). Canopus integrates it's discovery and loadbalancing capabilities with Circuit Breaker pattern into Feign in order to provide more features. Canopus also adds some new annotations for easy interaction with ```CircuitBreaker``` implementation.
```java
class Contributor {
    String login;
    int contributions;
}
interface GitHubClient {
    @Repeat(2)                                // in case of request failure, repeat request 2 times
    @Fallback(GitHubFallbackProvider.class)   // invoke GitHubFallbackProvider#contributors after 2 failed attempts
    @Timeout(10L)                             // request timeout and CircuitBreaker release timeout
    @RequestLine("GET /repos/{owner}/{repo}/contributors")
    List<Contributor> contributors(@Param("owner") String owner, @Param("repo") String repo);
}
class GitHubFallbackProvider implements GitHubClient {
    @Override
    public List<Contributor> contributors(@Param("owner") String owner, @Param("repo") String repo) {
        return Collections.emptyList();
    }
}

// Builds client without service discovering and load balancing, direct url
GitHub directGitHubClient = new Interaction().with("https://api.github.com")
                                             .using(new GsonDecoder())   // optional, GsonDecoder is default
                                             .using(new GsonEncoder())   // optional, default
                                             .via(GitHub.class);
List<Contributor> contributors = directGitHubClient.contributors("wingsofovnia", "canopus");

// GitHub client with dynamic service discovering and random load balancing
GitHub balancedGitHubServiceClient = new Interaction().with("myGitHubAPIMirrorService")
                                                      .using(new RandomLoadBalancer())
                                                      .using(new ConsulServiceDiscoverer("127.0.0.1"))
                                                      .via(GitHub.class);
List<Contributor> contributors = balancedGitHubServiceClient.contributors("wingsofovnia", "canopus");

// Manual RemoteService selection
ServiceDiscoverer serviceDiscoverer = new ConsulServiceDiscoverer("127.0.0.1");
List<RemoteService> myGitHubAPIMirrorServices = serviceDiscoverer.resolve("myGitHubAPIMirrorService");
RemoteService balancedMyGitHubAPIMirrorService = new RandomLoadBalancer().choose(myGitHubAPIMirrorServices);

GitHub manualBalancedGitHubServiceClient = new Interaction().with(balancedMyGitHubAPIMirrorService)
                                                            .via(GitHub.class);
List<Contributor> contributors = manualBalancedGitHubServiceClient.contributors("wingsofovnia", "canopus");
```

More information about Netflix Feign API you can find [here](https://github.com/Netflix/feign/blob/master/README.md).

## Bugs and Feedback
For bugs, questions and discussions please use the [Github Issues](https://github.com/wingsofovnia/canopus/issues).

## License
Except as otherwise noted this software is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
