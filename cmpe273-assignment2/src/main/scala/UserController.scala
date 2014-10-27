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
import org.springframework.beans.factory.annotation.Autowired
import com.mongodb.MongoClient
import com.mongodb.MongoException
import com.mongodb.WriteConcern
import com.mongodb.DB
import com.mongodb.DBCollection
import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import com.mongodb.DBCursor
import com.mongodb.ServerAddress
import com.mongodb.MongoClientURI;
import scala.collection.JavaConversions._
import org.springframework.web.client.RestTemplate
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


object UserController {
def main(args:Array[String])
{
SpringApplication.run(classOf[UserController])
}
}

@Controller
@EnableAutoConfiguration
@RequestMapping(Array("api/v1/users"))
class UserController  {
  var link:String ="mongodb://srinidhisrinivasaraghavan:sjsu273!@ds049130.mongolab.com:49130/test"
  var uri:MongoClientURI=new MongoClientURI(link)
  var mongoClient: MongoClient =new MongoClient(uri)
  var db:DB =mongoClient.getDB("test")
  var userCollection: DBCollection =db.getCollection("users");
  var idCardCollection: DBCollection =db.getCollection("idCards");
  var webLoginCollection: DBCollection =db.getCollection("webLogins");
  var bankAccountCollection: DBCollection =db.getCollection("bankAccounts");
 
  
 //start create user 
 @RequestMapping(value=Array(""),method =Array(RequestMethod.POST))
 @ResponseBody
 @ResponseStatus( HttpStatus.CREATED )
 def createUser(@Valid @RequestBody user :User) : User = {
 user.user_id="u-"+(userCollection.count()+1)
 var BasicUserObject:BasicDBObject = new BasicDBObject()
 BasicUserObject.put("user_id",user.user_id)
 BasicUserObject.put("email",user.email)
 BasicUserObject.put("password",user.password)
 if(user.name!=null)
 {
   BasicUserObject.put("name",user.name) 
 }
 BasicUserObject.put("created_at",user.created_at)
 BasicUserObject.put("updated_at",user.updated_at)
 userCollection.insert(BasicUserObject)
 return user;
 }//end of create user
 
 
//start of list user
@RequestMapping(value=Array("{user_id}"),method =Array(RequestMethod.GET))
@ResponseBody
def viewUser(@PathVariable user_id: String):ResponseEntity[User]= {
var searchQuery: BasicDBObject  = new BasicDBObject();
searchQuery.put("user_id", user_id) 
var userCount: Int=userCollection.find(searchQuery).count()
if(userCount!=0)
{
  var tempuser: User = new User() 
  var userCursor: DBCursor=userCollection.find(searchQuery)
  while(userCursor.hasNext())
  {
    var theUserObj: DBObject=userCursor.next()
    var theBasicUserObject: BasicDBObject = theUserObj.asInstanceOf[BasicDBObject]
    tempuser.user_id=user_id
    tempuser.email=theBasicUserObject.getString("email")
    tempuser.password =theBasicUserObject.getString("password")
    tempuser.name=theBasicUserObject.getString("name")
  }
  return new ResponseEntity[User](tempuser ,HttpStatus.OK);
}
  return new ResponseEntity[User]( HttpStatus.NOT_FOUND);
}//end of list user

//start of update user
@RequestMapping(value=Array("{user_id}"),method =Array(RequestMethod.PUT))
@ResponseBody
@ResponseStatus( HttpStatus.CREATED )
def updateUser(@PathVariable user_id: String,@RequestBody user :User): ResponseEntity[User] = {
var searchQuery: BasicDBObject  = new BasicDBObject();
searchQuery.put("user_id", user_id) 
var userCount: Int=userCollection.find(searchQuery).count()
if(userCount!=0)
{   
 var UserUpdateObject: BasicDBObject = new BasicDBObject();
 UserUpdateObject.append("user_id", user_id);
  var cursor:DBCursor = userCollection.find(searchQuery);
         while (cursor.hasNext()) {
          var theUserObj: DBObject=cursor.next()
          var theBasicUserObject: BasicDBObject = theUserObj.asInstanceOf[BasicDBObject]
          println(user_id==theBasicUserObject.getString("user_id")) 
          if(user_id==theBasicUserObject.getString("user_id"))
           {
            theBasicUserObject.put("email",user.email)
            theBasicUserObject.put("password",user.password)
            userCollection.update(UserUpdateObject,theBasicUserObject); 
         }} 
 var tempuser: User = new User()
 var userCursor: DBCursor=userCollection.find(searchQuery)
  while(userCursor.hasNext())
  {
    var theUserObj: DBObject=userCursor.next()
    var theBasicUserObject: BasicDBObject = theUserObj.asInstanceOf[BasicDBObject]
    tempuser.user_id=user_id
    tempuser.email=theBasicUserObject.getString("email")
    tempuser.password =theBasicUserObject.getString("password")
    if(theBasicUserObject.getString("name")!=null)
    tempuser.name=theBasicUserObject.getString("name")
    tempuser.created_at=theBasicUserObject.getString("created_at")
  }
  return new ResponseEntity[User](tempuser ,HttpStatus.CREATED);
}
  return new ResponseEntity[User]( HttpStatus.NOT_FOUND);
}
//end of update user



//start of create ID
@RequestMapping(value=Array({"{user_id}/idcards"}),method =Array(RequestMethod.POST))
@ResponseBody
@ResponseStatus( HttpStatus.CREATED )
def createIdCard(@PathVariable user_id: String,@Valid @RequestBody idcard :IDCard) : ResponseEntity[IDCard] = {
var searchQuery: BasicDBObject  = new BasicDBObject();
searchQuery.put("user_id", user_id) 
var userCount: Int=userCollection.find(searchQuery).count()
if(userCount!=0)
{
 //generating key to retrieve
 if(idCardCollection.count()>0){
 var theIdCardKeyCursor: DBCursor=idCardCollection.find().skip((idCardCollection.count()-1).asInstanceOf[Int])
 var theIdCardKeyObj: DBObject=theIdCardKeyCursor.next()
 var theBasicIdCardKeyObject: BasicDBObject = theIdCardKeyObj.asInstanceOf[BasicDBObject]
 var lastId: String = theBasicIdCardKeyObject.getString("card_id")
 lastId=lastId.replace("c","");
 lastId=lastId.replace("-","");
 idcard.card_id="c-"+(java.lang.Integer.parseInt(lastId)+1)}
 else
 idcard.card_id="c-"+1
 //end generating key to retrieve 
  
 var BasicCardObject:BasicDBObject = new BasicDBObject()
 BasicCardObject.put("card_id",idcard.card_id)
 BasicCardObject.put("user_id",user_id)
 BasicCardObject.put("card_name",idcard.card_name)
 BasicCardObject.put("card_number",idcard.card_number)
  if(idcard.expiration_date!=null)
 {
   BasicCardObject.put("expiration_date",idcard.expiration_date) 
 }
 idCardCollection.insert(BasicCardObject)
  return new ResponseEntity[IDCard](idcard ,HttpStatus.CREATED);
}
  return new ResponseEntity[IDCard]( HttpStatus.NOT_FOUND);
}//end of create id card


//start of list id card
@RequestMapping(value=Array("{user_id}/idcards"),method =Array(RequestMethod.GET))
@ResponseBody
def ListIdCards(@PathVariable user_id: String) : ResponseEntity[Array[IDCard]]= {
var searchQuery: BasicDBObject  = new BasicDBObject();
searchQuery.put("user_id", user_id) 
var userCount: Int=userCollection.find(searchQuery).count()
if(userCount!=0)
{
 var count:Int =0;
 var searchQuery: BasicDBObject  = new BasicDBObject();
 searchQuery.put("user_id", user_id) 
 var idCardCursor: DBCursor= idCardCollection.find(searchQuery)
 while(idCardCursor.hasNext())
 {
    count=count+1
    idCardCursor.next()
  }
  var tempid = Array.ofDim[IDCard](count)
  var j = 0;
  var idCardCursor1:DBCursor = idCardCollection.find(searchQuery)
  while(idCardCursor1.hasNext())
  {
          tempid(j) = new IDCard;
          var theIdCardObj: DBObject=idCardCursor1.next()
          var theBasicIdCardObject: BasicDBObject = theIdCardObj.asInstanceOf[BasicDBObject]
          tempid(j).user_id=theBasicIdCardObject.getString("user_id")
          tempid(j).card_id=theBasicIdCardObject.getString("card_id") 
          tempid(j).card_name=theBasicIdCardObject.getString("card_name")
          tempid(j).card_number=theBasicIdCardObject.getString("card_number")
          tempid(j).expiration_date=theBasicIdCardObject.getString("expiration_date")
          j=j+1
  }
  return new ResponseEntity[Array[IDCard]](tempid ,HttpStatus.OK);
}
  return new ResponseEntity[Array[IDCard]]( HttpStatus.NOT_FOUND);
}
//end of list the id cards

//start of delete id card 
@RequestMapping(value=Array("{user_id}/idcards/{card_id}"),method =Array(RequestMethod.DELETE))
@ResponseBody
def deleteIdCard(@PathVariable user_id: String,@PathVariable card_id: String) : ResponseEntity[IDCard] = {
var searchQuery: BasicDBObject  = new BasicDBObject();
searchQuery.put("user_id", user_id)
var userCount: Int=userCollection.find(searchQuery).count()
searchQuery.put("card_id", card_id)
var idCardCount: Int=idCardCollection.find(searchQuery).count()
if(userCount!=0 && idCardCount!=0)
{
var searchQuery: BasicDBObject  = new BasicDBObject();
searchQuery.put("card_id", card_id);
idCardCollection.remove(searchQuery)
  return new ResponseEntity[IDCard]( HttpStatus.NO_CONTENT);
}
  return new ResponseEntity[IDCard]( HttpStatus.NOT_FOUND);
}//end of delete ID

//start of add weblogin
@RequestMapping(value=Array({"{user_id}/weblogins"}),method =Array(RequestMethod.POST))
@ResponseBody
@ResponseStatus( HttpStatus.CREATED )
def createWeblogin(@PathVariable user_id: String,@Valid @RequestBody weblogin :WebLogin) : ResponseEntity[WebLogin] = {
var searchQuery: BasicDBObject  = new BasicDBObject();
searchQuery.put("user_id", user_id) 
var userCount: Int=userCollection.find(searchQuery).count()
if(userCount!=0)
{
 //generating key to retrieve
 if(webLoginCollection.count()>0){
 var theWebLoginKeyCursor: DBCursor=webLoginCollection.find().skip((webLoginCollection.count()-1).asInstanceOf[Int])
 var theWebLoginKeyObj: DBObject=theWebLoginKeyCursor.next()
 var theBasicWebLoginKeyObject: BasicDBObject = theWebLoginKeyObj.asInstanceOf[BasicDBObject]
 var lastId: String = theBasicWebLoginKeyObject.getString("login_id")
 lastId=lastId.replace("l","");
 lastId=lastId.replace("-","");
 weblogin.login_id="l-"+(java.lang.Integer.parseInt(lastId)+1)}
 else
 weblogin.login_id="l-"+1
 //end generating key to retrieve 
  
 var BasicWebLoginObject:BasicDBObject = new BasicDBObject()
 BasicWebLoginObject.put("login_id",weblogin.login_id)
 BasicWebLoginObject.put("user_id",user_id)
 BasicWebLoginObject.put("url",weblogin.url)
 BasicWebLoginObject.put("login",weblogin.login)
 BasicWebLoginObject.put("password",weblogin.password)
 webLoginCollection.insert(BasicWebLoginObject)

  return new ResponseEntity[WebLogin](weblogin ,HttpStatus.CREATED);
}
  return new ResponseEntity[WebLogin]( HttpStatus.NOT_FOUND);
}
//end of create weblogin

//start of list weblogin 
@RequestMapping(value=Array("{user_id}/weblogins"),method =Array(RequestMethod.GET))
@ResponseBody
def ListWebLogins(@PathVariable user_id: String) : ResponseEntity[Array[WebLogin]]= {
 var searchQuery: BasicDBObject  = new BasicDBObject();
searchQuery.put("user_id", user_id) 
var userCount: Int=userCollection.find(searchQuery).count()
if(userCount!=0)
{
 var count:Int =0;
 var webLoginCursor: DBCursor=webLoginCollection.find(searchQuery)
 while(webLoginCursor.hasNext())
 {
    count=count+1
    webLoginCursor.next()
  }
  var tempweblogin = Array.ofDim[WebLogin](count)
  var j = 0;
  var webLoginCursor1:DBCursor = webLoginCollection.find(searchQuery)
  while(webLoginCursor1.hasNext())
  {
          tempweblogin(j) = new WebLogin;
          var theWebLoginObj: DBObject=webLoginCursor1.next()
          var theBasicWebLoginObject: BasicDBObject = theWebLoginObj.asInstanceOf[BasicDBObject]
          tempweblogin(j).user_id=theBasicWebLoginObject.getString("user_id")
          tempweblogin(j).login_id=theBasicWebLoginObject.getString("login_id") 
          tempweblogin(j).url=theBasicWebLoginObject.getString("url")
          tempweblogin(j).login=theBasicWebLoginObject.getString("login")
          tempweblogin(j).password=theBasicWebLoginObject.getString("password")
          j=j+1
  }
return new ResponseEntity[Array[WebLogin]](tempweblogin ,HttpStatus.OK);
}
  return new ResponseEntity[Array[WebLogin]]( HttpStatus.NOT_FOUND);
}
//end of list the weblogins2


//start of delete weblogins
@RequestMapping(value=Array("{user_id}/weblogins/{login_id}"),method =Array(RequestMethod.DELETE))
@ResponseBody
def deleteWebLogin(@PathVariable user_id: String,@PathVariable login_id: String) : ResponseEntity[WebLogin] = {
var searchQuery: BasicDBObject  = new BasicDBObject();
searchQuery.put("user_id", user_id)
var userCount: Int=userCollection.find(searchQuery).count()
searchQuery.put("login_id", login_id)
var webLoginCount: Int=webLoginCollection.find(searchQuery).count()
if(userCount!=0 && webLoginCount!=0)
{
var searchQuery: BasicDBObject  = new BasicDBObject();
searchQuery.put("login_id", login_id);
webLoginCollection.remove(searchQuery)
return new ResponseEntity[WebLogin](HttpStatus.NO_CONTENT);  
}
 return new ResponseEntity[WebLogin]( HttpStatus.NOT_FOUND);   
}
//end of delete weblogins


 //start of create bankaccount
@RequestMapping(value=Array({"{user_id}/bankaccounts"}),method =Array(RequestMethod.POST))
@ResponseBody
@ResponseStatus( HttpStatus.CREATED )
def createBankAccount(@PathVariable user_id: String,@Valid @RequestBody bankaccount :BankAccount) : ResponseEntity[BankAccount] = {
var searchQuery: BasicDBObject  = new BasicDBObject();
searchQuery.put("user_id", user_id) 
var userCount: Int=userCollection.find(searchQuery).count()
if(userCount!=0)
{
 var response:ResponseEntity[String] =test(bankaccount.routing_number,bankaccount)
 var code:HttpStatus=response.getStatusCode()
 if(code==HttpStatus.OK)
 {
 //generating key to retrieve
 if(bankAccountCollection.count()>0){

 var theBankAccountKeyCursor: DBCursor=bankAccountCollection.find().skip((bankAccountCollection.count()-1).asInstanceOf[Int])
 var theBankAccountKeyObj: DBObject=theBankAccountKeyCursor.next()
 var theBasicBankAccountKeyObject: BasicDBObject = theBankAccountKeyObj.asInstanceOf[BasicDBObject]
 var lastId: String = theBasicBankAccountKeyObject.getString("ba_id")
 lastId=lastId.replace("b","");
 lastId=lastId.replace("-","");
 bankaccount.ba_id="b-"+(java.lang.Integer.parseInt(lastId)+1)}
 else
 bankaccount.ba_id="b-"+1
 //end generating key to retrieve 
  
 var BasicBankAccountObject:BasicDBObject = new BasicDBObject()
 BasicBankAccountObject.put("ba_id",bankaccount.ba_id)
 BasicBankAccountObject.put("user_id",user_id)

 BasicBankAccountObject.put("routing_number",bankaccount.routing_number)
 BasicBankAccountObject.put("account_number",bankaccount.account_number)

   BasicBankAccountObject.put("account_name",bankaccount.account_name) 
 
 bankAccountCollection.insert(BasicBankAccountObject)

   return new ResponseEntity[BankAccount](bankaccount ,HttpStatus.CREATED);
}
return new ResponseEntity[BankAccount]( HttpStatus.NOT_FOUND);
} 

 return new ResponseEntity[BankAccount]( HttpStatus.NOT_FOUND);
}
//end of create bankaccount


 //start of list bankaccount
@RequestMapping(value=Array("{user_id}/bankaccounts"),method =Array(RequestMethod.GET))
@ResponseBody
def ListBankAccounts(@PathVariable user_id: String) :ResponseEntity[Array[BankAccount]]= {
var searchQuery: BasicDBObject  = new BasicDBObject();
searchQuery.put("user_id", user_id) 
var userCount: Int=userCollection.find(searchQuery).count()
if(userCount!=0)
{
 var count:Int =0;
 var bankAccountCursor: DBCursor=bankAccountCollection.find(searchQuery)
 while(bankAccountCursor.hasNext())
 {
    count=count+1
    bankAccountCursor.next()
  }
  var tempbankaccount = Array.ofDim[BankAccount](count)
  var j = 0;
  var bankAccountCursor1:DBCursor = bankAccountCollection.find(searchQuery)
  while(bankAccountCursor1.hasNext())
  {
          tempbankaccount(j) = new BankAccount();
          var thebankAccountObj: DBObject=bankAccountCursor1.next()
          var theBasicBankAccountObject: BasicDBObject = thebankAccountObj.asInstanceOf[BasicDBObject]
          tempbankaccount(j).user_id=theBasicBankAccountObject.getString("user_id")
          tempbankaccount(j).ba_id=theBasicBankAccountObject.getString("ba_id") 
          tempbankaccount(j).account_name=theBasicBankAccountObject.getString("account_name")
          tempbankaccount(j).routing_number=theBasicBankAccountObject.getString("routing_number")
          tempbankaccount(j).account_number=theBasicBankAccountObject.getString("account_number")
          j=j+1
  }
return new ResponseEntity[Array[BankAccount]](tempbankaccount ,HttpStatus.OK);
}
 return new ResponseEntity[Array[BankAccount]]( HttpStatus.NOT_FOUND); 
}
//end of list the bankaccounts


//start of delete bankaccounts
@RequestMapping(value=Array("{user_id}/bankaccounts/{ba_id}"),method =Array(RequestMethod.DELETE))
@ResponseBody
def deleteBankAccount(@PathVariable user_id: String,@PathVariable ba_id: String): ResponseEntity[BankAccount] = {
var searchQuery: BasicDBObject  = new BasicDBObject();
searchQuery.put("user_id", user_id)
var userCount: Int=bankAccountCollection.find(searchQuery).count()
searchQuery.put("ba_id", ba_id)
var bankAccountCount: Int=bankAccountCollection.find(searchQuery).count()
if(userCount!=0 && bankAccountCount!=0)
{
var searchQuery: BasicDBObject  = new BasicDBObject();
searchQuery.put("ba_id", ba_id);
bankAccountCollection.remove(searchQuery)
return new ResponseEntity[BankAccount](HttpStatus.NO_CONTENT);
}
return new ResponseEntity[BankAccount]( HttpStatus.NOT_FOUND); 
}
//end of delete bankaccounts
@ResponseStatus( HttpStatus.CREATED)
@ResponseBody
def test(routing_number:String,bankaccount :BankAccount):ResponseEntity[String] = {
var restTemplate: RestTemplate = new RestTemplate();
var url: String  = "http://www.routingnumbers.info/api/data.json?rn="+routing_number;
val entity = restTemplate.getForEntity(url, classOf[String])
val body = entity.getBody
val jsonParser: JSONParser= new JSONParser();
val jsonObject:JSONObject  = jsonParser.parse(entity.getBody).asInstanceOf[JSONObject];
val code: Long= jsonObject.get("code").asInstanceOf[Long];
if(bankaccount.account_name==null)
{
val customer_name=jsonObject.get("customer_name")
bankaccount.account_name=customer_name.asInstanceOf[String];
}
if(code==200)
{
System.out.println("The status code is: " +code);
println(body)
return new ResponseEntity[String](HttpStatus.OK);
}
return new ResponseEntity[String](HttpStatus.NOT_FOUND);
}
}