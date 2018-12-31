package com.example.demo

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

object PingActor {
  case object Ping
  def props(pingPongProxy: ActorRef) = Props(new PingActor(pingPongProxy))
}

class PingActor(pingPongProxy: ActorRef) extends Actor with ActorLogging {
  import PingActor._
  import PingPongProxy._
  import PongActor._

  pingPongProxy ! IamPing(self)
  val limit = 3

  override def receive: Receive = waitingForPong

  def waitingForPong: Receive = {

    case PongTo(pong: ActorRef) =>
      log.info("Proxy said, pong to {}", pong.path)
      context.become(ready(pong, limit))
      pong ! Pong

    case ACK =>
      log.info("Ping ... ACK")
      pingPongProxy ! WhoToPong

    case Ping =>
      log.info("*** Unexpected Message, waitingForPong >> WhoToPong")
      pingPongProxy ! WhoToPong

    case x => log.info("*** Unexpected Message, waitingForPong >> {} ", x)

  }

  def ready(pongRef: ActorRef, lmt: Int): Receive = {
    case Ping =>
      log.info("<<ping>> <{}>", lmt)
      context.become(ready(pongRef, lmt-1))
      pongRef ! Pong

    case x => log.info("*** Unexpected Message, ready >> {} ", x)
  }

  override def postStop(): Unit = {
    super.postStop()
    log.info("***  Ping Actor stopped!!! ")
  }
}