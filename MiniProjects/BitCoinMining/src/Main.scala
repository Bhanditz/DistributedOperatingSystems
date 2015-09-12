import akka.actor._
import akka.actor.{Props, ActorSystem}

/**
 * Created by PRATEEK on 9/12/2015.
 */

object Main extends App{
  override def main (args: Array[String]) {
      if(args(0).length() > 7){
       println("Okay I m the Remote IP")
        val system=ActorSystem("Bitcoin-server")
        val clientActor = system.actorOf(Props(new RemoteClients(args(0))),"masterActor")
      }
      else{
        println("Hello I am in Here")
        val system=ActorSystem("Bitcoin-server")
        println("Hello I am in Here")
        val masterActor = system.actorOf(Props(new BitcoinServer()),"masterActor")
        masterActor ! MineCoins
        system.awaitTermination()
      }
  }
}

