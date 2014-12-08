package main.scala
import javax.validation.Valid
import java.lang.Long
import scala.beans.BeanProperty
import org.springframework.boot._
import org.springframework.boot.autoconfigure._
import org.springframework.stereotype._
import org.springframework.web.bind.annotation._
import java.util._
import scala.collection.JavaConversions._
import java.sql.Timestamp
import collection.mutable.HashMap
import java.text.SimpleDateFormat
import org.springframework.http.HttpStatus
import org.codehaus.jackson.annotate.JsonIgnoreProperties
import org.codehaus.jackson.map.annotate.JsonSerialize
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

import java.net.URI
import java.util.UUID
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import com.google.common.util.concurrent.ListenableFuture
import com.justinsb.etcd.EtcdClient
import com.justinsb.etcd.EtcdClientException
import com.justinsb.etcd.EtcdResult

object UserController {
def main(args:Array[String])
{
SpringApplication.run(classOf[UserController])
}
}


@Controller
@EnableAutoConfiguration
@RequestMapping(Array("api/v1"))
class UserController {
  var client: EtcdClient=new EtcdClient(URI.create("http://54.183.5.19:4001/"));
  var counter:String="0";
  var userid:Int =1;
  var idcardid: Int=1;
  var webloginid: Int=1;
  var bankaccountid:Int=1;
 var map = scala.collection.mutable.Map[String, User]();
 var mapidcard=scala.collection.mutable.Map[String, IDCard]();
 var mapweblogin=scala.collection.mutable.Map[String, WebLogin]();
 var mapbankaccount=scala.collection.mutable.Map[String, BankAccount]();
 
var key = "/009440730/counter"
// var result = this.client.set(key, counter)
var result=this.client.get(key);
 
 //start counter
 @RequestMapping(value=Array("/counter"),method =Array(RequestMethod.GET))
 @ResponseBody
 @ResponseStatus( HttpStatus.OK )
 def getCounter() : String = {
 result = this.client.get(key)
// println("counter value"+result.value)
 var counter_value: Int =Integer.parseInt(result.value);
 counter_value = counter_value +1
 counter = ""+counter_value
 result = this.client.set(key, counter)
 result = this.client.get(key)
 
 println("counter value"+result.value)
 return counter;
 }
 
 //for health
 @RequestMapping(value=Array("/healthCheck"),method =Array(RequestMethod.GET))
 @ResponseBody
 @ResponseStatus( HttpStatus.OK )
 def getHealth() : String = {
 return ""
 }
 //end 
 





 
 //start create user
 @RequestMapping(value=Array("/users"),method =Array(RequestMethod.POST))
 @ResponseBody
 @ResponseStatus( HttpStatus.CREATED )
 def createUser(@Valid @RequestBody user :User) : User = {
 user.user_id="u-"+userid 
 userid=userid+1
 map.put(user.user_id,user);
 return user;
 }
 //end of create user
 
 //start of list user
@RequestMapping(value=Array("/users/{user_id}"),method =Array(RequestMethod.GET))
@ResponseBody
def viewUser(@PathVariable user_id: String): ResponseEntity[User]= {
  if(map.contains(user_id))
    
  {
   var tempusr: User = new User() 
   val set = map.entrySet()
   val i = set.iterator()
   while (i.hasNext) {
      val me = i.next()
      if(me.getValue().user_id==user_id)
      {
        tempusr.user_id=me.getValue().user_id 
        tempusr.email=me.getValue().email 
        tempusr.password=me.getValue.password 
        tempusr.created_at =me.getValue().created_at
        if(me.getValue().name!=null)
        {
          tempusr.name =me.getValue().name
        }
    }
   }
 return new ResponseEntity[User](tempusr ,HttpStatus.OK);;
 }
return new ResponseEntity[User]( HttpStatus.NOT_FOUND);
}
//end of list user

//start of update user
@RequestMapping(value=Array("/users/{user_id}"),method =Array(RequestMethod.PUT))
@ResponseBody
@ResponseStatus( HttpStatus.CREATED )
def updateUser(@PathVariable user_id: String,@RequestBody user :User): ResponseEntity[User] = {
  if(map.contains(user_id))
    
  {
var tempusr: User = new User() 
val set = map.entrySet()
val i = set.iterator()
while (i.hasNext) {
     
      val me = i.next()
      if(me.getValue().user_id==user_id)
      {
        val dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val date = new Date()
        me.getValue().email=user.email 
        me.getValue().password=user.password
        me.getValue().updated_at=dateFormat.format(date)
        tempusr.user_id=me.getValue().user_id 
        tempusr.email=me.getValue().email 
        tempusr.password=me.getValue.password 
        tempusr.created_at =me.getValue().created_at
        tempusr.updated_at =me.getValue().updated_at 
    	
       }
  }
return new ResponseEntity[User](tempusr ,HttpStatus.CREATED);  
}
return new ResponseEntity[User]( HttpStatus.NOT_FOUND); 
}
//end of update user

//start of create ID
@RequestMapping(value=Array({"/users/{user_id}/idcards"}),method =Array(RequestMethod.POST))
@ResponseBody
@ResponseStatus( HttpStatus.CREATED )
def createIdCard(@PathVariable user_id: String,@Valid @RequestBody idcard :IDCard) : ResponseEntity[IDCard] = {
  
  if(map.contains(user_id))
    
  {
 idcard.card_id="c-"+idcardid 
 idcardid=idcardid+1
  idcard.user_id=user_id; 
  mapidcard.put(idcard.card_id,idcard);
   
   
   return new ResponseEntity[IDCard](idcard ,HttpStatus.CREATED);
  }
  return new ResponseEntity[IDCard]( HttpStatus.NOT_FOUND);
}
//end of create id card

//start of list id card
@RequestMapping(value=Array("/users/{user_id}/idcards"),method =Array(RequestMethod.GET))
@ResponseBody
def ListIdCards(@PathVariable user_id: String) : ResponseEntity[Array[IDCard]]= {
  if(map.contains(user_id))
    
  {
  var count:Int =0;
  val fset = mapidcard.entrySet()
  val k = fset.iterator()
   while (k.hasNext) {
     val me = k.next()
     if(user_id==me.getValue().user_id)
       count=count+1 
   }
  var tempid = Array.ofDim[IDCard](count)
  var j = 0;
  val set = mapidcard.entrySet()
  val i = set.iterator()
   while (i.hasNext) {
        
        val me = i.next()        
        if(me.getValue().user_id==user_id){
          
          tempid(j) = new IDCard;
          tempid(j).user_id=me.getValue().user_id
          tempid(j).card_id=me.getValue().card_id 
          tempid(j).card_name=me.getValue().card_name 
          tempid(j).card_number=me.getValue.card_number
          if(me.getValue().expiration_date!=null)
        {
          tempid(j).expiration_date =me.getValue().expiration_date
        }
          j = j +1;
          //tempid.expiration_date =me.getValue().created_at
        }
}
  return new ResponseEntity[Array[IDCard]](tempid ,HttpStatus.OK);  
}
 return new ResponseEntity[Array[IDCard]]( HttpStatus.NOT_FOUND);
}
//end of list the id cards

//start of delete id card 
@RequestMapping(value=Array("/users/{user_id}/idcards/{card_id}"),method =Array(RequestMethod.DELETE))
@ResponseBody
def deleteIdCard(@PathVariable user_id: String,@PathVariable card_id: String) : ResponseEntity[IDCard] = {
  
   if(mapidcard.contains(card_id) && map.contains(user_id) ) 
  { 
  val set = mapidcard.entrySet()
   val i = set.iterator()
   while (i.hasNext) {
     
      val me = i.next()
      if(me.getValue().user_id==user_id)
      {
  mapidcard.remove(card_id)
  
  
}
}
 return new ResponseEntity[IDCard](HttpStatus.NO_CONTENT); 
}
return new ResponseEntity[IDCard]( HttpStatus.NOT_FOUND); 
}
//end of delete ID

//start of add weblogin
@RequestMapping(value=Array({"/users/{user_id}/weblogins"}),method =Array(RequestMethod.POST))
@ResponseBody
@ResponseStatus( HttpStatus.CREATED )
def createWeblogin(@PathVariable user_id: String,@Valid @RequestBody weblogin :WebLogin) : ResponseEntity[WebLogin] = {
  if(map.contains(user_id))
    
  {
  weblogin.login_id="l-"+webloginid 
 webloginid=webloginid+1
  weblogin.user_id=user_id; 
  mapweblogin.put(weblogin.login_id,weblogin);

  return new ResponseEntity[WebLogin](weblogin ,HttpStatus.CREATED);
}
  return new ResponseEntity[WebLogin]( HttpStatus.NOT_FOUND);
}
//end of create weblogin

//start of list weblogin 
@RequestMapping(value=Array("/users/{user_id}/weblogins"),method =Array(RequestMethod.GET))
@ResponseBody
def ListWebLogins(@PathVariable user_id: String) : ResponseEntity[Array[WebLogin]]= {
  if(map.contains(user_id))
    
  {
  var count:Int =0;
  val fset = mapweblogin.entrySet()
  val k = fset.iterator()
   while (k.hasNext) {
     val me = k.next()
     if(user_id==me.getValue().user_id)
       count=count+1
     
   }
  var tempweb = Array.ofDim[WebLogin](count)
  var j = 0;
  val set = mapweblogin.entrySet()
  val i = set.iterator()
   while (i.hasNext) {
       
        val me = i.next()  
        if(me.getValue().user_id==user_id){
          println("inside if list idcards")
          tempweb(j) = new WebLogin
          tempweb(j).user_id=me.getValue().user_id
          tempweb(j).login_id=me.getValue().login_id 
          tempweb(j).url=me.getValue().url 
          tempweb(j).login=me.getValue.login
          tempweb(j).pssword=me.getValue.pssword
          j = j +1;
          //tempid.expiration_date =me.getValue().created_at
        }
   }

return new ResponseEntity[Array[WebLogin]](tempweb ,HttpStatus.OK);
}
  return new ResponseEntity[Array[WebLogin]]( HttpStatus.NOT_FOUND);
}
//end of list the weblogins

//start of delete weblogins
@RequestMapping(value=Array("/users/{user_id}/weblogins/{login_id}"),method =Array(RequestMethod.DELETE))
@ResponseBody
def deleteWebLogin(@PathVariable user_id: String,@PathVariable login_id: String) : ResponseEntity[WebLogin] = {
   if(mapweblogin.contains(login_id) && map.contains(user_id)) 
  {  
  val set = mapweblogin.entrySet()
   val i = set.iterator()
   while (i.hasNext) {
     
      val me = i.next()
      if(me.getValue().user_id==user_id)
      {
  mapweblogin.remove(login_id)
 
  }
}
return new ResponseEntity[WebLogin](HttpStatus.NO_CONTENT);  
}
 return new ResponseEntity[WebLogin]( HttpStatus.NOT_FOUND);   
}
//end of delete weblogins

//start of create bankaccount
@RequestMapping(value=Array({"/users/{user_id}/bankaccounts"}),method =Array(RequestMethod.POST))
@ResponseBody
@ResponseStatus( HttpStatus.CREATED )
def createBankAccount(@PathVariable user_id: String,@Valid @RequestBody bankaccount :BankAccount) : ResponseEntity[BankAccount] = {
 if(map.contains(user_id))
    
  {
  bankaccount.ba_id="b-"+bankaccountid 
 bankaccountid=bankaccountid+1
  bankaccount.user_id=user_id; 
  mapbankaccount.put(bankaccount.ba_id,bankaccount);
  
   return new ResponseEntity[BankAccount](bankaccount ,HttpStatus.CREATED);
}
 return new ResponseEntity[BankAccount]( HttpStatus.NOT_FOUND);
}
//end of create bankaccount

//start of list bankaccount
@RequestMapping(value=Array("/users/{user_id}/bankaccounts"),method =Array(RequestMethod.GET))
@ResponseBody
def ListBankAccounts(@PathVariable user_id: String) :ResponseEntity[Array[BankAccount]]= {
  if(map.contains(user_id))
    
  {
  var count:Int =0;
  val fset = mapbankaccount.entrySet()
  val k = fset.iterator()
   while (k.hasNext) {
     val me = k.next()
     if(user_id==me.getValue().user_id)
       count=count+1
     
   }
  
var tempbankaccount = Array.ofDim[BankAccount](count)
  var j = 0;
  val set = mapbankaccount.entrySet()
  val i = set.iterator()
   while (i.hasNext) {
        
        val me = i.next()        
        if(me.getValue().user_id==user_id){
          
          tempbankaccount(j) = new BankAccount;
           tempbankaccount(j).user_id=me.getValue().user_id
           tempbankaccount(j).ba_id=me.getValue().ba_id
           tempbankaccount(j).account_name=me.getValue().account_name 
           tempbankaccount(j).routing_number=me.getValue().routing_number 
           tempbankaccount(j).account_number=me.getValue.account_number
           j = j +1;
        }
}
return new ResponseEntity[Array[BankAccount]](tempbankaccount ,HttpStatus.OK);
}
 return new ResponseEntity[Array[BankAccount]]( HttpStatus.NOT_FOUND); 
}
//end of list the bankaccounts

//start of delete bankaccounts
@RequestMapping(value=Array("/users/{user_id}/bankaccounts/{ba_id}"),method =Array(RequestMethod.DELETE))
@ResponseBody
def deleteBankAccount(@PathVariable user_id: String,@PathVariable ba_id: String): ResponseEntity[BankAccount] = {
   if(mapbankaccount.contains(ba_id) && map.contains(user_id)) 
  { 
  val set = mapbankaccount.entrySet()
   val i = set.iterator()
   while (i.hasNext) {
     
      val me = i.next()
      if(me.getValue().user_id==user_id)
      {
  mapbankaccount.remove(ba_id)
 
}
}
  return new ResponseEntity[BankAccount](HttpStatus.NO_CONTENT);
}
   return new ResponseEntity[BankAccount]( HttpStatus.NOT_FOUND); 
}
//end of delete bankaccounts
}
