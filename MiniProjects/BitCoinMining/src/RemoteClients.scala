import akka.actor.Props
import akka.routing.RoundRobinPool
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._
import akka.actor.ReceiveTimeout
import akka.actor.RootActorPath
import akka.actor.Identify
import akka.actor.ActorIdentity
import akka.actor.ActorLogging
import akka.actor.Terminated
import akka.actor._
import scala.util.Random
/**
 * author: prateek.j1
 * Created on: 09/10/2015
 */
case class peformWork(start: Int, quantity: Int, leadingZeros: Int, gatorId: String, actorId: Int, inputString: String)
case class GetCoins(coinList: ListBuffer[Tuple2[String, String]] )
case object noWork

class RemoteClients(ipAddress: String) extends Actor with ActorLogging{
  val main = new Address("akka.tcp","BitCoinMining", ipAddress, 2552)
  val noOfWorkers: Int = 8
  val remote = context.actorSelection(RootActorPath(main) / "user" / "masterServer")
  remote ! clientConnected
  def receive = {
    case "Start" =>
        println("I am Up")
    case peformWork(start, quantity, leadingZeros, gatorId, actorId, inputString) =>
        println ("Client is ready to do the work")
        val remoteWorker = context.actorOf(RoundRobinPool(noOfWorkers).props(Props(new Worker())), "scheduleRemoteWorker")
        remoteWorker ! Work(start, quantity, leadingZeros, gatorId, actorId, inputString, true)
    case GetCoins(list: ListBuffer[Tuple2[String, String]])=>
        remote ! Result(list)
    case noWork =>
        context.system.shutdown()
  }
}
