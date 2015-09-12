import java.security.MessageDigest
import akka.actor.{ActorLogging, Actor}
import scala.collection.mutable.ListBuffer

case class Work(start: Int, quantity: Int, leadingZeros: Int, gatorId: String, actorId: Int, inputString: String, isRemoteClient: Boolean)
class Worker extends Actor with ActorLogging{
  def findCoins(start:Int, quantity:Int, leadingZeros: Int, gatorID: String, actorId: Int, inputString: String):ListBuffer[Tuple2[String, String]]  ={
    return digestSHA256(inputString, leadingZeros, gatorID, start, quantity, actorId)
  }
  def digestSHA256(input: String, zeros: Int,  gatorId: String, start: Int, quantity: Int, actorId: Int) : ListBuffer[Tuple2[String, String]] = {
    var hasZeroes: String = ""
    var bitCoins = new ListBuffer[Tuple2[String, String]]()
    for (i <- 1 to zeros)
      hasZeroes += "0"
    for(attempts <- start to quantity){
      val s : String = gatorId +";" +input  + attempts.toString()+ actorId.toString()
      val digest : String = MessageDigest.getInstance("SHA-256").digest(s.getBytes)
        .foldLeft("")((s: String, b: Byte) => s + Character.forDigit((b & 0xf0) >> 4, 16) +
        Character.forDigit(b & 0x0f, 16))
      if(digest.startsWith(hasZeroes)){
        //println("Digest" + digest)
        bitCoins += ((s, digest))
      }
    }
    return bitCoins
  }
  def receive={
    case Work(start, quantity, leadingZeros, gatorId, actorId, inputString, isRemoteClient)=>
      if(!isRemoteClient)
        sender ! Result(findCoins(start, quantity, leadingZeros, gatorId, actorId, inputString))
      else
        sender ! GetCoins(findCoins(start, quantity, leadingZeros, gatorId, actorId, inputString))
  }
}
