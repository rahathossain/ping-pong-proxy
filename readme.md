# Ping Pong Proxy

This is a of simple Ping Pong example between two actors, 
`PingActor` and `PongActor`. 
However, there's a 3rd actor called `PingPongProxy` 
will help these two actors to introduce each other, 
and after the introduction `PingActor` and `PongActor` talk to each other. 

Application start with `DemoApplication.scala`, 
in which we create one actor system. 
In that actor system, we will first create `PingPongProxy` actor, 
then we create `PingActor` and `PongActor` in that actor system. 

We create the `PingPongProxy` first, 
then use it's reference while creating `PingActor` and `PongActor`, 
see details in the below `DemoApplication.scala` 


```scala
object DemoApplication extends App {

  val actorSystem = ActorSystem("DemoActorSystem")
  val pingPongProxy = actorSystem.actorOf(Props[PingPongProxy], "PiPoProxy")
  actorSystem.actorOf(PingActor.props(pingPongProxy), "PingActor")
  actorSystem.actorOf(Props(new PongActor(pingPongProxy)), "PongActor")
  
}
```


