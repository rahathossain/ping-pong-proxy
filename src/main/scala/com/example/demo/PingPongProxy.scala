package com.example.demo

import akka.actor.{Actor, ActorLogging, ActorRef}

//# - Start PingPongProxy - companion object
object PingPongProxy {
  case object ACK
  case class IamPing(ping: ActorRef)
  case class IamPong(pong: ActorRef)
  case object WhoToPing
  case object WhoToPong
  case class PingTo(ping: ActorRef)
  case class PongTo(pong: ActorRef)
}
//# - End PingPongProxy - companion object


//# - Start PingPongProxy - Actor
class PingPongProxy extends Actor with ActorLogging {
import PingPongProxy._

  override def receive: Receive = waitingForBothPingAndPong

  def waitingForBothPingAndPong: Receive = {

    case IamPing(ping: ActorRef) =>
      log.info("Ping First said 'I am Ping'")
      ping ! ACK
      context.become(waitingForPongOnly(ping))

    case IamPong(pong: ActorRef) =>
      log.info("Pong First said 'I am Pong'")
      pong ! ACK
      context.become(waitingForPingOnly(pong))

    case _ => log.info("****** Unexpected. waitingForBothPingAndPong")
  }


  def waitingForPongOnly(ping: ActorRef): Receive = {
    case IamPong(pong: ActorRef) =>
      log.info("Pong Second to to say 'I am Pong'")
      pong ! ACK
      context.become(ready(ping, pong))
    case WhoToPong =>
      log.info("****** Not sure WhoToPong, WaitingForPongOnly >> Try again later")
      ping ! ACK
    case x =>
      log.info("****** Unexpected. waitingForPongOnly >> "+x)
  }

  def waitingForPingOnly(pong: ActorRef): Receive = {
    case IamPing(ping: ActorRef) =>
      log.info("Ping is 2nd to say 'I am Ping'")
      ping ! ACK
      context.become(ready(ping, pong))
    case WhoToPing =>
      log.info("****** Not sure WhoToPing, WaitingForPongOnly >> Try again later")
      pong ! ACK
    case x => log.info("****** Unexpected. waitingForPingOnly >> "+ x)
  }

  def ready(ping: ActorRef, pong: ActorRef): Receive = {
    case WhoToPing =>
      log.info("Received who is Ping query")
      pong ! PingTo(ping)
    case WhoToPong =>
      log.info("Received who is Pong query")
      ping ! PongTo(pong)
    case x =>
      log.info("BAD LUCK!" + x)
  }

  override def postStop(): Unit = {
    super.postStop()
    log.info("***  PingPongProxy Actor stopped!!! ")
  }


}
