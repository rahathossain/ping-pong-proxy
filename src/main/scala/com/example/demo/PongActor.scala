package com.example.demo

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill}


object PongActor {
  case object Pong
}

class PongActor(pingPongProxy: ActorRef) extends Actor with ActorLogging {
  import PongActor._
  import PingPongProxy._
  import  PingActor._

  pingPongProxy ! IamPong(self)
  val limit = 3

  override def receive: Receive = waitingForPing

  def waitingForPing: Receive = {
    case PingTo(ping: ActorRef) =>
      val p = ping.path
      log.info(s"Proxy said, ping to $p")
      context.become(ready(ping, limit))
      ping ! Ping

    case ACK =>
      log.info("Pong ... ACK")
      pingPongProxy ! WhoToPing

    case Pong =>
      log.info("*** Unexpected Message, waitingForPing >> waitingForPing ")
      pingPongProxy ! WhoToPing

    case x => log.info("*** Unexpected Message, waitingForPing >> {} ", x)
  }

  def ready(pingRef: ActorRef, lmt: Int): Receive = {
    case Pong =>
      log.info(">>pong<< {}", lmt)
      context.become(ready(pingRef, lmt-1))
      if(lmt > 0 ) {
        pingRef ! Ping
      } else {
        pingRef ! PoisonPill
        pingPongProxy ! PoisonPill
        self ! PoisonPill
      }

    case x => log.info("*** Unexpected Message, ready >> {} ", x)
  }

  override def postStop(): Unit = {
    super.postStop()
    log.info("***  Pong Actor stopped!!! ")
  }
}
