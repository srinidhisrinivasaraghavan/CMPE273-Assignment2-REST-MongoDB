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
import org.codehaus.jackson.map.annotate.JsonSerialize

@Entity
class WebLogin  {

@Id 
@GeneratedValue 
@BeanProperty
var login_id: String = _

var user_id: String = _

@BeanProperty
@NotEmpty
var url: String = _
  
@BeanProperty
@NotEmpty
var login: String = _

@BeanProperty
@NotEmpty
var pssword: String = _

}