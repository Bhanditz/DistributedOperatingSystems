import akka.actor.{ActorSystem,Actor,ActorLogging,Props}
import akka.routing.RoundRobinPool
import java.security.MessageDigest
import collection.mutable.ListBuffer

case object MineCoins
case object clientConnected

//case class Work(start: Int, quantity: Int, leadinZeros: Int)
case class Result(coinList: ListBuffer[Tuple2[String, String]] )
class BitcoinServer extends Actor with ActorLogging{

  val startTime = System.currentTimeMillis()
  val gatorId : String = "adobra"
  val inputString : String = "Hello"
  var actorId : Int = 1
  var currentTasks: Int =_
  val noOfWorkers: Int = 10
  val totalTask : Int = 100000
  val noOfTasks: Int = 1000000000
  var startTask : Int = 0
  var taskLeft : Int =  totalTask
  val sizeOfWork: Int = 1000
  val leadingZeros = 3
  var duration: Double =_
  val workerScheduler = context.actorOf(RoundRobinPool(noOfWorkers).props(Props(new Worker())), "schedule")
  var coins = new ListBuffer[Tuple2[String, String]]()
  def displaySolution(coins: ListBuffer[Tuple2[String, String]]) = {
    for(coin: Tuple2[String, String]<-coins){
      println(coin._1+" bitcoin is "+coin._2)
    }
  }
  def receive={
    case MineCoins=>
      println("Okay I m here")
//     for(i<-0 to noOfTasks){
//        workerScheduler ! Work(i*sizeOfWork, sizeOfWork, leadingZeros, gatorId, self.path.toString, inputString, false)
//      }
      workerScheduler ! Work(startTask, startTask + sizeOfWork, leadingZeros, gatorId, 0, inputString, false)
      startTask += sizeOfWork
      taskLeft -= startTask

    case Result(list)=>
      for(coin:Tuple2[String, String]<-list){
        coins+=coin
      }
      println (" Got Result")
      if(taskLeft > 0)
        self ! MineCoins
      else {
          displaySolution(coins)
          duration=(System.currentTimeMillis()-startTime)/1000d
          println("Time taken=%s seconds".format(duration))
      }


    case clientConnected =>
        if( taskLeft <= 0)
          sender ! noWork
        else
          sender ! peformWork(startTask, startTask + sizeOfWork + 1000, leadingZeros, gatorId, actorId, inputString)
        startTask += sizeOfWork
        taskLeft -= startTask
        actorId += 1
  }
}