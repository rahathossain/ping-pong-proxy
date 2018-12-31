package com.example.demo

import akka.actor.{ActorSystem, Props}

object DemoApplication extends App {

  val actorSystem = ActorSystem("DemoActorSystem")
  val pingPongProxy = actorSystem.actorOf(Props[PingPongProxy], "PiPoProxy")
  actorSystem.actorOf(PingActor.props(pingPongProxy), "PingActor")
  actorSystem.actorOf(Props(new PongActor(pingPongProxy)), "PongActor")

}