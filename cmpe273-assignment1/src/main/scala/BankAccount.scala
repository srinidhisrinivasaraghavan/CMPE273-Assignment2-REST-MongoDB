package main.scala
import javax.persistence.Id
import javax.persistence.GeneratedValue
import java.lang.Long
import javax.persistence.Entity
import scala.beans.BeanProperty
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.boot._
import org.springframework.boot.autoconfigure._
import org.springframework.stereotype._
import org.springframework.web.bind.annotation._
import java.util._
import scala.collection.JavaConversions._
import java.sql.Timestamp

@Entity
class BankAccount {

@Id 
@GeneratedValue 
@BeanProperty
var ba_id: String = _

@BeanProperty
var account_name: String = _

var user_id: String = _
  
@BeanProperty
@NotEmpty
var routing_number: String = _

@BeanProperty
@NotEmpty
var account_number: String = _




  
}