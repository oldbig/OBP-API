package code.bankconnectors.akka

import java.util.Date

import akka.pattern.ask
import code.actorsystem.ObpLookupSystem
import code.api.ResourceDocs1_4_0.MessageDocsSwaggerDefinitions
import code.api.util.APIUtil.{AdapterImplementation, MessageDoc, OBPReturnType, parseDate}
import code.api.util.ExampleValue._
import code.api.util._
import code.bankconnectors._
import code.bankconnectors.akka.actor.{AkkaConnectorActorInit, AkkaConnectorHelperActor}
import com.openbankproject.commons.ExecutionContext.Implicits.global
import com.openbankproject.commons.dto._
import com.openbankproject.commons.model._
import com.openbankproject.commons.model.enums.StrongCustomerAuthentication.SCA
import com.openbankproject.commons.model.enums.{AccountAttributeType, CardAttributeType, ProductAttributeType}
import net.liftweb.common.Box

import scala.collection.immutable.List
import scala.concurrent.Future

object AkkaConnector_vDec2018 extends Connector with AkkaConnectorActorInit {

  implicit override val nameOfConnector = AkkaConnector_vDec2018.toString
  val messageFormat: String = "Dec2018"
  
  lazy val southSideActor = ObpLookupSystem.getAkkaConnectorActor(AkkaConnectorHelperActor.actorName)

  /*messageDocs += MessageDoc(
    process = "obp.getAdapterInfo",
    messageFormat = messageFormat,
    description = "Gets information about the active general (non bank specific) Adapter that is responding to messages sent by OBP.",
    outboundTopic = Some(OutBoundGetAdapterInfo.getClass.getSimpleName.replace("$", "")),
    inboundTopic = Some(InBoundGetAdapterInfo.getClass.getSimpleName.replace("$", "")),
    exampleOutboundMessage = (
      OutBoundGetAdapterInfo(
        outboundAdapterCallContext
    )),
    exampleInboundMessage = (
      InBoundGetAdapterInfo(
        inboundAdapterCallContext,
        inboundStatus,
        inboundAdapterInfoInternal)
    ),
    outboundAvroSchema = Some(parse(SchemaFor[OutBoundGetAdapterInfo]().toString(true))),
    inboundAvroSchema = Some(parse(SchemaFor[InBoundGetAdapterInfo]().toString(true))),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )
  override def getAdapterInfo(callContext: Option[CallContext]): Future[Box[(InboundAdapterInfoInternal, Option[CallContext])]] = {
    val req = OutBoundGetAdapterInfo(callContext.map(_.toOutboundAdapterCallContext).get)
    val response = (southSideActor ? req).mapTo[InBoundGetAdapterInfo]
    response.map(r => Full(r.data, callContext))
  }

  messageDocs += MessageDoc(
    process = "obp.getBanks",
    messageFormat = messageFormat,
    description = "Gets the banks list on this OBP installation.",
    outboundTopic = Some(OutBoundGetBanks.getClass.getSimpleName.replace("$", "")),
    inboundTopic = Some(InBoundGetBanks.getClass.getSimpleName.replace("$", "")),
    exampleOutboundMessage = (
      OutBoundGetBanks(outboundAdapterCallContext)
    ),
    exampleInboundMessage = (
      InBoundGetBanks(
        inboundAdapterCallContext,
        inboundStatus,
        List(bankCommons)
        )
    ),
    outboundAvroSchema = Some(parse(SchemaFor[OutBoundGetBanks]().toString(true))),
    inboundAvroSchema =  Some(parse(SchemaFor[InBoundGetBanks]().toString(true))),
    adapterImplementation = Some(AdapterImplementation("- Core", 2))
  )
  
  override def getBanks(callContext: Option[CallContext]): Future[Box[(List[Bank], Option[CallContext])]] = {
    val req = OutBoundGetBanks(callContext.map(_.toOutboundAdapterCallContext).get)
    val response: Future[InBoundGetBanks] = (southSideActor ? req).mapTo[InBoundGetBanks]
    response.map(r => Full(r.data, callContext))
  }

  messageDocs += MessageDoc(
    process = "obp.getBank",
    messageFormat = messageFormat,
    description = "Get a specific Bank as specified by bankId",
    outboundTopic = Some(OutBoundGetBank.getClass.getSimpleName.replace("$", "")),
    inboundTopic = Some(InBoundGetBank.getClass.getSimpleName.replace("$", "")),
    exampleOutboundMessage = (
      OutBoundGetBank(
        outboundAdapterCallContext,
        BankId(bankIdExample.value))
    ),
    exampleInboundMessage = (
      InBoundGetBank(
        inboundAdapterCallContext,
        inboundStatus,
        bankCommons
      )
    ),
    outboundAvroSchema = Some(parse(SchemaFor[OutBoundGetBank]().toString(true))),
    inboundAvroSchema = Some(parse(SchemaFor[InBoundGetBank]().toString(true))),
    adapterImplementation = Some(AdapterImplementation("- Core", 5))
  )
  override def getBank(bankId : BankId, callContext: Option[CallContext]): Future[Box[(Bank, Option[CallContext])]] = {
    val req = OutBoundGetBank(callContext.map(_.toOutboundAdapterCallContext).get, bankId)
    val response: Future[InBoundGetBank] = (southSideActor ? req).mapTo[InBoundGetBank]
    response.map(r => Full(r.data, callContext))
  }

   messageDocs += MessageDoc(
    process = "obp.getBankAccountsForUser",
    messageFormat = messageFormat,
    description = "Gets the list of accounts available to the User. This call sends authInfo including username.",
    outboundTopic = Some(OutBoundGetBankAccountsForUser.getClass.getSimpleName.replace("$", "")),
    inboundTopic = Some(InBoundGetBankAccountsForUser.getClass.getSimpleName.replace("$", "")),
    exampleOutboundMessage = (
      OutBoundGetBankAccountsForUser(
        outboundAdapterCallContext,
        usernameExample.value)
    ),
    exampleInboundMessage = (
      InBoundGetBankAccountsForUser(
        inboundAdapterCallContext,
        inboundStatus,
        List(inboundAccountCommons)
      )
    ),
    adapterImplementation = Some(AdapterImplementation("Accounts", 5))
  )
  override def getBankAccountsForUser(username: String, callContext: Option[CallContext]): Future[Box[(List[InboundAccount], Option[CallContext])]] = {
    val req = OutBoundGetBankAccountsForUser(callContext.map(_.toOutboundAdapterCallContext).get, username)
    val response = (southSideActor ? req).mapTo[InBoundGetBankAccountsForUser]
    response.map(a =>(Full(a.data, callContext)))
  }
  
  messageDocs += MessageDoc(
    process = "obp.checkBankAccountExists",
    messageFormat = messageFormat,
    description = "Check a bank Account exists - as specified by bankId and accountId.",
    outboundTopic = Some(OutBoundCheckBankAccountExists.getClass.getSimpleName.replace("$", "")),
    inboundTopic = Some(InBoundCheckBankAccountExists.getClass.getSimpleName.replace("$", "")),
    exampleOutboundMessage = (
      OutBoundCheckBankAccountExists(
        outboundAdapterCallContext,
        BankId(bankIdExample.value),
        AccountId(accountIdExample.value)
      )
    ),
    exampleInboundMessage = (
      InBoundCheckBankAccountExists(
        inboundAdapterCallContext,
        inboundStatus,
        bankAccountCommons
      )
    ),
    adapterImplementation = Some(AdapterImplementation("Accounts", 4))
  )
  override def checkBankAccountExists(bankId : BankId, accountId : AccountId, callContext: Option[CallContext] = None) = {
    val req = OutBoundCheckBankAccountExists(callContext.map(_.toOutboundAdapterCallContext).get, bankId, accountId)
    val response: Future[InBoundCheckBankAccountExists] = (southSideActor ? req).mapTo[InBoundCheckBankAccountExists]
    response.map(a =>(Full(a.data), callContext))
    
  }

  messageDocs += MessageDoc(
    process = "obp.getBankAccount",
    messageFormat = messageFormat,
    description = "Get a single Account as specified by the bankId and accountId.",
    outboundTopic = Some(OutBoundGetBankAccount.getClass.getSimpleName.replace("$", "")),
    inboundTopic = Some(InBoundGetBankAccount.getClass.getSimpleName.replace("$", "")),
    exampleOutboundMessage = (
      OutBoundGetBankAccount(
        outboundAdapterCallContext,
        BankId(bankIdExample.value),
        AccountId(accountIdExample.value),
      )
    ),
    exampleInboundMessage = (
      InBoundGetBankAccount(
        inboundAdapterCallContext,
        inboundStatus,
        bankAccountCommons
      )
    ),
    adapterImplementation = Some(AdapterImplementation("Accounts", 7))
  )
  override def getBankAccount(bankId : BankId, accountId : AccountId, callContext: Option[CallContext]): OBPReturnType[Box[BankAccount]] = {
    val req = OutBoundGetBankAccount(callContext.map(_.toOutboundAdapterCallContext).get, bankId, accountId)
    val response = (southSideActor ? req).mapTo[InBoundGetBankAccount]
    response.map(a =>(Full(a.data), callContext))
  }

  messageDocs += MessageDoc(
    process = "obp.getCoreBankAccounts",
    messageFormat = messageFormat,
    description = "Get bank Accounts available to the User (without Metadata)",
    outboundTopic = Some(OutBoundGetCoreBankAccounts.getClass.getSimpleName.replace("$", "")),
    inboundTopic = Some(InBoundGetCoreBankAccounts.getClass.getSimpleName.replace("$", "")),
    exampleOutboundMessage = (
      OutBoundGetCoreBankAccounts(
        outboundAdapterCallContext,
        List(BankIdAccountId(BankId(bankIdExample.value), AccountId(accountIdExample.value)))
      )
    ),
    exampleInboundMessage = (
      InBoundGetCoreBankAccounts(
        inboundAdapterCallContext,
        inboundStatus,
        List(
          CoreAccount(
            accountIdExample.value, 
            "My private account for Uber", 
            bankIdExample.value, 
            accountTypeExample.value, 
            List(AccountRouting(accountRoutingSchemeExample.value, accountRoutingAddressExample.value)
            )
          )
        )
      )),
    adapterImplementation = Some(AdapterImplementation("Accounts", 1))
  )
  override def getCoreBankAccounts(BankIdAccountIds: List[BankIdAccountId], callContext: Option[CallContext]) : Future[Box[(List[CoreAccount], Option[CallContext])]] = {
    val req = OutBoundGetCoreBankAccounts(callContext.map(_.toOutboundAdapterCallContext).get, BankIdAccountIds) 
    val response = (southSideActor ? req).mapTo[InBoundGetCoreBankAccounts]
    response.map(a =>(Full(a.data, callContext)))
  }



  messageDocs += MessageDoc(
    process = "obp.getCustomersByUserId",
    messageFormat = messageFormat,
    description = "Get Customers represented by the User.",
    outboundTopic = Some(OutBoundGetCustomersByUserId.getClass.getSimpleName.replace("$", "")),
    inboundTopic = Some(InBoundGetCustomersByUserId.getClass.getSimpleName.replace("$", "")),
    exampleOutboundMessage = (
      OutBoundGetCustomersByUserId(
        outboundAdapterCallContext,
        userIdExample.value
      )
    ),
    exampleInboundMessage = (
      InBoundGetCustomersByUserId(
        inboundAdapterCallContext,
        inboundStatus,
        customerCommons:: Nil,
      )
    ),
    outboundAvroSchema = None,
    inboundAvroSchema = None,
    adapterImplementation = Some(AdapterImplementation("Accounts", 0))
  )
  override def getCustomersByUserId(userId: String, callContext: Option[CallContext]): Future[Box[(List[Customer], Option[CallContext])]] = {
    val req = OutBoundGetCustomersByUserId(callContext.map(_.toOutboundAdapterCallContext).get, userId)
    val response= (southSideActor ? req).mapTo[InBoundGetCustomersByUserId]
    response.map(a =>(Full(a.data, callContext)))
  }

  messageDocs += MessageDoc(
    process = "obp.getTransactions",
    messageFormat = messageFormat,
    description = "Get Transactions for an Account specified by bankId and accountId. Pagination is achieved with limit, fromDate and toDate.",
    outboundTopic = Some(OutBoundGetTransactions.getClass.getSimpleName.replace("$", "")),
    inboundTopic = Some(InBoundGetTransactions.getClass.getSimpleName.replace("$", "")),
    exampleOutboundMessage = (
      OutBoundGetTransactions(
        outboundAdapterCallContext,
        bankId = BankId(bankIdExample.value),
        accountId = AccountId(accountIdExample.value),
        limit = limitExample.value.toInt,
        offset = offsetExample.value.toInt,
        fromDate = APIUtil.DateWithDayExampleString, 
        toDate = APIUtil.DateWithDayExampleString) 
    ),
    exampleInboundMessage = (
      InBoundGetTransactions(
        inboundAdapterCallContext,
        inboundStatus,
        List(transactionCommons)
      )
    ),
    adapterImplementation = Some(AdapterImplementation("Transactions", 10))
  )
  override def getTransactions(bankId: BankId, accountId: AccountId, callContext: Option[CallContext], queryParams: List[OBPQueryParam]): OBPReturnType[Box[List[Transaction]]] = {
    val limit = queryParams.collect { case OBPLimit(value) => value }.headOption.getOrElse(100)
    val offset = queryParams.collect { case OBPOffset(value) => value }.headOption.getOrElse(0)
    val fromDate = queryParams.collect { case OBPFromDate(date) => APIUtil.DateWithMsFormat.format(date) }.headOption.getOrElse(APIUtil.DefaultFromDate.toString)
    val toDate = queryParams.collect { case OBPToDate(date) => APIUtil.DateWithMsFormat.format(date) }.headOption.getOrElse(APIUtil.DefaultToDate.toString)

    val req = OutBoundGetTransactions(callContext.map(_.toOutboundAdapterCallContext).get, bankId, accountId, limit, offset, fromDate, toDate)
    val response: Future[InBoundGetTransactions] = (southSideActor ? req).mapTo[InBoundGetTransactions]
    response.map(a =>(Full(a.data), callContext))
  }

  messageDocs += MessageDoc(
    process = "obp.getTransaction",
    messageFormat = messageFormat,
    description = "Get a single Transaction specified by bankId, accountId and transactionId",
    outboundTopic = Some(OutBoundGetTransaction.getClass.getSimpleName.replace("$", "")),
    inboundTopic = Some(InBoundGetTransaction.getClass.getSimpleName.replace("$", "")),
    exampleOutboundMessage = (
      OutBoundGetTransaction(
        outboundAdapterCallContext,
        bankId = BankId(bankIdExample.value),
        accountId = AccountId(accountIdExample.value),
        transactionId = TransactionId(transactionIdExample.value)
      )
    ),
    exampleInboundMessage = (
      InBoundGetTransaction(
        inboundAdapterCallContext,
        inboundStatus,
        transactionCommons
      )
    ),
    adapterImplementation = Some(AdapterImplementation("Transactions", 11))
  )
  override def getTransaction(bankId: BankId, accountId: AccountId, transactionId: TransactionId, callContext: Option[CallContext]): OBPReturnType[Box[Transaction]] = {
    val req = OutBoundGetTransaction(callContext.map(_.toOutboundAdapterCallContext).get, bankId, accountId, transactionId)
    val response= (southSideActor ? req).mapTo[InBoundGetTransaction]
    response.map(a =>(Full(a.data), callContext))
  }*/

//---------------- dynamic start -------------------please don't modify this line
// ---------- create on Mon Jun 15 12:48:47 CST 2020

  messageDocs += getAdapterInfoDoc
  def getAdapterInfoDoc = MessageDoc(
    process = "obp.getAdapterInfo",
    messageFormat = messageFormat,
    description = "Get Adapter Info",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
          OutBoundGetAdapterInfo(MessageDocsSwaggerDefinitions.outboundAdapterCallContext)
    ),
    exampleInboundMessage = (
     InBoundGetAdapterInfo(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= InboundAdapterInfoInternal(errorCode=inboundAdapterInfoInternalErrorCodeExample.value,
      backendMessages=List( InboundStatusMessage(source=sourceExample.value,
      status=inboundStatusMessageStatusExample.value,
      errorCode=inboundStatusMessageErrorCodeExample.value,
      text=inboundStatusMessageTextExample.value)),
      name=inboundAdapterInfoInternalNameExample.value,
      version=inboundAdapterInfoInternalVersionExample.value,
      git_commit=inboundAdapterInfoInternalGit_commitExample.value,
      date=inboundAdapterInfoInternalDateExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getAdapterInfo(callContext: Option[CallContext]): Future[Box[(InboundAdapterInfoInternal, Option[CallContext])]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetAdapterInfo => InBound, OutBoundGetAdapterInfo => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull )
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[InboundAdapterInfoInternal](callContext))


  }
          
  messageDocs += getChallengeThresholdDoc
  def getChallengeThresholdDoc = MessageDoc(
    process = "obp.getChallengeThreshold",
    messageFormat = messageFormat,
    description = "Get Challenge Threshold",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetChallengeThreshold(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=bankIdExample.value,
      accountId=accountIdExample.value,
      viewId=viewIdExample.value,
      transactionRequestType=transactionRequestTypeExample.value,
      currency=currencyExample.value,
      userId=userIdExample.value,
      userName="string")
    ),
    exampleInboundMessage = (
     InBoundGetChallengeThreshold(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= AmountOfMoney(currency=currencyExample.value,
      amount="string"))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getChallengeThreshold(bankId: String, accountId: String, viewId: String, transactionRequestType: String, currency: String, userId: String, userName: String, callContext: Option[CallContext]): OBPReturnType[Box[AmountOfMoney]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetChallengeThreshold => InBound, OutBoundGetChallengeThreshold => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, accountId, viewId, transactionRequestType, currency, userId, userName)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[AmountOfMoney](callContext))


  }
          
  messageDocs += getChargeLevelDoc
  def getChargeLevelDoc = MessageDoc(
    process = "obp.getChargeLevel",
    messageFormat = messageFormat,
    description = "Get Charge Level",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetChargeLevel(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      accountId=AccountId(accountIdExample.value),
      viewId=ViewId(viewIdExample.value),
      userId=userIdExample.value,
      userName="string",
      transactionRequestType=transactionRequestTypeExample.value,
      currency=currencyExample.value)
    ),
    exampleInboundMessage = (
     InBoundGetChargeLevel(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= AmountOfMoney(currency=currencyExample.value,
      amount="string"))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getChargeLevel(bankId: BankId, accountId: AccountId, viewId: ViewId, userId: String, userName: String, transactionRequestType: String, currency: String, callContext: Option[CallContext]): OBPReturnType[Box[AmountOfMoney]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetChargeLevel => InBound, OutBoundGetChargeLevel => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, accountId, viewId, userId, userName, transactionRequestType, currency)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[AmountOfMoney](callContext))


  }
          
  messageDocs += createChallengeDoc
  def createChallengeDoc = MessageDoc(
    process = "obp.createChallenge",
    messageFormat = messageFormat,
    description = "Create Challenge",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundCreateChallenge(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      accountId=AccountId(accountIdExample.value),
      userId=userIdExample.value,
      transactionRequestType=TransactionRequestType(transactionRequestTypeExample.value),
      transactionRequestId="string",
      scaMethod=Some(com.openbankproject.commons.model.enums.StrongCustomerAuthentication.SMS))
    ),
    exampleInboundMessage = (
     InBoundCreateChallenge(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data="string")
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def createChallenge(bankId: BankId, accountId: AccountId, userId: String, transactionRequestType: TransactionRequestType, transactionRequestId: String, scaMethod: Option[SCA], callContext: Option[CallContext]): OBPReturnType[Box[String]] = {
    

        import com.openbankproject.commons.dto.{InBoundCreateChallenge => InBound, OutBoundCreateChallenge => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, accountId, userId, transactionRequestType, transactionRequestId, scaMethod)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[String](callContext))


  }
          
  messageDocs += validateChallengeAnswerDoc
  def validateChallengeAnswerDoc = MessageDoc(
    process = "obp.validateChallengeAnswer",
    messageFormat = messageFormat,
    description = "Validate Challenge Answer",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundValidateChallengeAnswer(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      challengeId="string",
      hashOfSuppliedAnswer="string")
    ),
    exampleInboundMessage = (
     InBoundValidateChallengeAnswer(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=true)
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def validateChallengeAnswer(challengeId: String, hashOfSuppliedAnswer: String, callContext: Option[CallContext]): OBPReturnType[Box[Boolean]] = {
    

        import com.openbankproject.commons.dto.{InBoundValidateChallengeAnswer => InBound, OutBoundValidateChallengeAnswer => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , challengeId, hashOfSuppliedAnswer)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[Boolean](callContext))


  }
          
  messageDocs += getBankLegacyDoc
  def getBankLegacyDoc = MessageDoc(
    process = "obp.getBankLegacy",
    messageFormat = messageFormat,
    description = "Get Bank Legacy",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetBankLegacy(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value))
    ),
    exampleInboundMessage = (
     InBoundGetBankLegacy(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= BankCommons(bankId=BankId(bankIdExample.value),
      shortName=bankShortNameExample.value,
      fullName=bankFullNameExample.value,
      logoUrl=bankLogoUrlExample.value,
      websiteUrl=bankWebsiteUrlExample.value,
      bankRoutingScheme=bankRoutingSchemeExample.value,
      bankRoutingAddress=bankRoutingAddressExample.value,
      swiftBic=bankSwiftBicExample.value,
      nationalIdentifier=bankNationalIdentifierExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getBankLegacy(bankId: BankId, callContext: Option[CallContext]): Box[(Bank, Option[CallContext])] = {
    

        import com.openbankproject.commons.dto.{InBoundGetBankLegacy => InBound, OutBoundGetBankLegacy => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[BankCommons](callContext))


  }
          
  messageDocs += getBankDoc
  def getBankDoc = MessageDoc(
    process = "obp.getBank",
    messageFormat = messageFormat,
    description = "Get Bank",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetBank(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value))
    ),
    exampleInboundMessage = (
     InBoundGetBank(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= BankCommons(bankId=BankId(bankIdExample.value),
      shortName=bankShortNameExample.value,
      fullName=bankFullNameExample.value,
      logoUrl=bankLogoUrlExample.value,
      websiteUrl=bankWebsiteUrlExample.value,
      bankRoutingScheme=bankRoutingSchemeExample.value,
      bankRoutingAddress=bankRoutingAddressExample.value,
      swiftBic=bankSwiftBicExample.value,
      nationalIdentifier=bankNationalIdentifierExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getBank(bankId: BankId, callContext: Option[CallContext]): Future[Box[(Bank, Option[CallContext])]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetBank => InBound, OutBoundGetBank => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[BankCommons](callContext))


  }
          
  messageDocs += getBanksLegacyDoc
  def getBanksLegacyDoc = MessageDoc(
    process = "obp.getBanksLegacy",
    messageFormat = messageFormat,
    description = "Get Banks Legacy",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
          OutBoundGetBanksLegacy(MessageDocsSwaggerDefinitions.outboundAdapterCallContext)
    ),
    exampleInboundMessage = (
     InBoundGetBanksLegacy(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( BankCommons(bankId=BankId(bankIdExample.value),
      shortName=bankShortNameExample.value,
      fullName=bankFullNameExample.value,
      logoUrl=bankLogoUrlExample.value,
      websiteUrl=bankWebsiteUrlExample.value,
      bankRoutingScheme=bankRoutingSchemeExample.value,
      bankRoutingAddress=bankRoutingAddressExample.value,
      swiftBic=bankSwiftBicExample.value,
      nationalIdentifier=bankNationalIdentifierExample.value)))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getBanksLegacy(callContext: Option[CallContext]): Box[(List[Bank], Option[CallContext])] = {
    

        import com.openbankproject.commons.dto.{InBoundGetBanksLegacy => InBound, OutBoundGetBanksLegacy => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull )
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[BankCommons]](callContext))


  }
          
  messageDocs += getBanksDoc
  def getBanksDoc = MessageDoc(
    process = "obp.getBanks",
    messageFormat = messageFormat,
    description = "Get Banks",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
          OutBoundGetBanks(MessageDocsSwaggerDefinitions.outboundAdapterCallContext)
    ),
    exampleInboundMessage = (
     InBoundGetBanks(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( BankCommons(bankId=BankId(bankIdExample.value),
      shortName=bankShortNameExample.value,
      fullName=bankFullNameExample.value,
      logoUrl=bankLogoUrlExample.value,
      websiteUrl=bankWebsiteUrlExample.value,
      bankRoutingScheme=bankRoutingSchemeExample.value,
      bankRoutingAddress=bankRoutingAddressExample.value,
      swiftBic=bankSwiftBicExample.value,
      nationalIdentifier=bankNationalIdentifierExample.value)))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getBanks(callContext: Option[CallContext]): Future[Box[(List[Bank], Option[CallContext])]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetBanks => InBound, OutBoundGetBanks => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull )
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[BankCommons]](callContext))


  }
          
  messageDocs += getBankAccountsForUserLegacyDoc
  def getBankAccountsForUserLegacyDoc = MessageDoc(
    process = "obp.getBankAccountsForUserLegacy",
    messageFormat = messageFormat,
    description = "Get Bank Accounts For User Legacy",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetBankAccountsForUserLegacy(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      username=usernameExample.value)
    ),
    exampleInboundMessage = (
     InBoundGetBankAccountsForUserLegacy(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( InboundAccountCommons(bankId=bankIdExample.value,
      branchId=branchIdExample.value,
      accountId=accountIdExample.value,
      accountNumber=accountNumberExample.value,
      accountType=accountTypeExample.value,
      balanceAmount=balanceAmountExample.value,
      balanceCurrency=balanceCurrencyExample.value,
      owners=inboundAccountOwnersExample.value.split("[,;]").toList,
      viewsToGenerate=inboundAccountViewsToGenerateExample.value.split("[,;]").toList,
      bankRoutingScheme=bankRoutingSchemeExample.value,
      bankRoutingAddress=bankRoutingAddressExample.value,
      branchRoutingScheme=branchRoutingSchemeExample.value,
      branchRoutingAddress=branchRoutingAddressExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value)))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getBankAccountsForUserLegacy(username: String, callContext: Option[CallContext]): Box[(List[InboundAccount], Option[CallContext])] = {
    

        import com.openbankproject.commons.dto.{InBoundGetBankAccountsForUserLegacy => InBound, OutBoundGetBankAccountsForUserLegacy => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , username)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[InboundAccountCommons]](callContext))


  }
          
  messageDocs += getBankAccountsForUserDoc
  def getBankAccountsForUserDoc = MessageDoc(
    process = "obp.getBankAccountsForUser",
    messageFormat = messageFormat,
    description = "Get Bank Accounts For User",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetBankAccountsForUser(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      username=usernameExample.value)
    ),
    exampleInboundMessage = (
     InBoundGetBankAccountsForUser(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( InboundAccountCommons(bankId=bankIdExample.value,
      branchId=branchIdExample.value,
      accountId=accountIdExample.value,
      accountNumber=accountNumberExample.value,
      accountType=accountTypeExample.value,
      balanceAmount=balanceAmountExample.value,
      balanceCurrency=balanceCurrencyExample.value,
      owners=inboundAccountOwnersExample.value.split("[,;]").toList,
      viewsToGenerate=inboundAccountViewsToGenerateExample.value.split("[,;]").toList,
      bankRoutingScheme=bankRoutingSchemeExample.value,
      bankRoutingAddress=bankRoutingAddressExample.value,
      branchRoutingScheme=branchRoutingSchemeExample.value,
      branchRoutingAddress=branchRoutingAddressExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value)))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getBankAccountsForUser(username: String, callContext: Option[CallContext]): Future[Box[(List[InboundAccount], Option[CallContext])]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetBankAccountsForUser => InBound, OutBoundGetBankAccountsForUser => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , username)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[InboundAccountCommons]](callContext))


  }
          
  messageDocs += getBankAccountLegacyDoc
  def getBankAccountLegacyDoc = MessageDoc(
    process = "obp.getBankAccountLegacy",
    messageFormat = messageFormat,
    description = "Get Bank Account Legacy",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetBankAccountLegacy(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      accountId=AccountId(accountIdExample.value))
    ),
    exampleInboundMessage = (
     InBoundGetBankAccountLegacy(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=bankAccountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getBankAccountLegacy(bankId: BankId, accountId: AccountId, callContext: Option[CallContext]): Box[(BankAccount, Option[CallContext])] = {
    

        import com.openbankproject.commons.dto.{InBoundGetBankAccountLegacy => InBound, OutBoundGetBankAccountLegacy => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, accountId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[BankAccountCommons](callContext))


  }
          
  messageDocs += getBankAccountDoc
  def getBankAccountDoc = MessageDoc(
    process = "obp.getBankAccount",
    messageFormat = messageFormat,
    description = "Get Bank Account",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetBankAccount(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      accountId=AccountId(accountIdExample.value))
    ),
    exampleInboundMessage = (
     InBoundGetBankAccount(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=bankAccountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getBankAccount(bankId: BankId, accountId: AccountId, callContext: Option[CallContext]): OBPReturnType[Box[BankAccount]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetBankAccount => InBound, OutBoundGetBankAccount => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, accountId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[BankAccountCommons](callContext))


  }
          
  messageDocs += getBankAccountByIbanDoc
  def getBankAccountByIbanDoc = MessageDoc(
    process = "obp.getBankAccountByIban",
    messageFormat = messageFormat,
    description = "Get Bank Account By Iban",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetBankAccountByIban(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      iban=ibanExample.value)
    ),
    exampleInboundMessage = (
     InBoundGetBankAccountByIban(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=bankAccountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getBankAccountByIban(iban: String, callContext: Option[CallContext]): OBPReturnType[Box[BankAccount]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetBankAccountByIban => InBound, OutBoundGetBankAccountByIban => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , iban)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[BankAccountCommons](callContext))


  }
          
  messageDocs += getBankAccountByRoutingDoc
  def getBankAccountByRoutingDoc = MessageDoc(
    process = "obp.getBankAccountByRouting",
    messageFormat = messageFormat,
    description = "Get Bank Account By Routing",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetBankAccountByRouting(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      scheme="string",
      address="string")
    ),
    exampleInboundMessage = (
     InBoundGetBankAccountByRouting(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=bankAccountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getBankAccountByRouting(scheme: String, address: String, callContext: Option[CallContext]): Box[(BankAccount, Option[CallContext])] = {
    

        import com.openbankproject.commons.dto.{InBoundGetBankAccountByRouting => InBound, OutBoundGetBankAccountByRouting => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , scheme, address)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[BankAccountCommons](callContext))


  }
          
  messageDocs += getBankAccountsDoc
  def getBankAccountsDoc = MessageDoc(
    process = "obp.getBankAccounts",
    messageFormat = messageFormat,
    description = "Get Bank Accounts",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetBankAccounts(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankIdAccountIds=List( BankIdAccountId(bankId=BankId(bankIdExample.value),
      accountId=AccountId(accountIdExample.value))))
    ),
    exampleInboundMessage = (
     InBoundGetBankAccounts(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=bankAccountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value)))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getBankAccounts(bankIdAccountIds: List[BankIdAccountId], callContext: Option[CallContext]): OBPReturnType[Box[List[BankAccount]]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetBankAccounts => InBound, OutBoundGetBankAccounts => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankIdAccountIds)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[BankAccountCommons]](callContext))


  }
          
  messageDocs += getBankAccountsBalancesDoc
  def getBankAccountsBalancesDoc = MessageDoc(
    process = "obp.getBankAccountsBalances",
    messageFormat = messageFormat,
    description = "Get Bank Accounts Balances",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetBankAccountsBalances(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankIdAccountIds=List( BankIdAccountId(bankId=BankId(bankIdExample.value),
      accountId=AccountId(accountIdExample.value))))
    ),
    exampleInboundMessage = (
     InBoundGetBankAccountsBalances(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= AccountsBalances(accounts=List( AccountBalance(id=accountIdExample.value,
      label=labelExample.value,
      bankId=bankIdExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      balance= AmountOfMoney(currency=balanceCurrencyExample.value,
      amount=balanceAmountExample.value))),
      overallBalance= AmountOfMoney(currency=currencyExample.value,
      amount="string"),
      overallBalanceDate=new Date()))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getBankAccountsBalances(bankIdAccountIds: List[BankIdAccountId], callContext: Option[CallContext]): OBPReturnType[Box[AccountsBalances]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetBankAccountsBalances => InBound, OutBoundGetBankAccountsBalances => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankIdAccountIds)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[AccountsBalances](callContext))


  }
          
  messageDocs += getCoreBankAccountsLegacyDoc
  def getCoreBankAccountsLegacyDoc = MessageDoc(
    process = "obp.getCoreBankAccountsLegacy",
    messageFormat = messageFormat,
    description = "Get Core Bank Accounts Legacy",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetCoreBankAccountsLegacy(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankIdAccountIds=List( BankIdAccountId(bankId=BankId(bankIdExample.value),
      accountId=AccountId(accountIdExample.value))))
    ),
    exampleInboundMessage = (
     InBoundGetCoreBankAccountsLegacy(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( CoreAccount(id=accountIdExample.value,
      label=labelExample.value,
      bankId=bankIdExample.value,
      accountType=accountTypeExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)))))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getCoreBankAccountsLegacy(bankIdAccountIds: List[BankIdAccountId], callContext: Option[CallContext]): Box[(List[CoreAccount], Option[CallContext])] = {
    

        import com.openbankproject.commons.dto.{InBoundGetCoreBankAccountsLegacy => InBound, OutBoundGetCoreBankAccountsLegacy => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankIdAccountIds)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[CoreAccount]](callContext))


  }
          
  messageDocs += getCoreBankAccountsDoc
  def getCoreBankAccountsDoc = MessageDoc(
    process = "obp.getCoreBankAccounts",
    messageFormat = messageFormat,
    description = "Get Core Bank Accounts",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetCoreBankAccounts(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankIdAccountIds=List( BankIdAccountId(bankId=BankId(bankIdExample.value),
      accountId=AccountId(accountIdExample.value))))
    ),
    exampleInboundMessage = (
     InBoundGetCoreBankAccounts(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( CoreAccount(id=accountIdExample.value,
      label=labelExample.value,
      bankId=bankIdExample.value,
      accountType=accountTypeExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)))))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getCoreBankAccounts(bankIdAccountIds: List[BankIdAccountId], callContext: Option[CallContext]): Future[Box[(List[CoreAccount], Option[CallContext])]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetCoreBankAccounts => InBound, OutBoundGetCoreBankAccounts => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankIdAccountIds)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[CoreAccount]](callContext))


  }
          
  messageDocs += getBankAccountsHeldLegacyDoc
  def getBankAccountsHeldLegacyDoc = MessageDoc(
    process = "obp.getBankAccountsHeldLegacy",
    messageFormat = messageFormat,
    description = "Get Bank Accounts Held Legacy",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetBankAccountsHeldLegacy(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankIdAccountIds=List( BankIdAccountId(bankId=BankId(bankIdExample.value),
      accountId=AccountId(accountIdExample.value))))
    ),
    exampleInboundMessage = (
     InBoundGetBankAccountsHeldLegacy(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( AccountHeld(id="string",
      bankId=bankIdExample.value,
      number="string",
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)))))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getBankAccountsHeldLegacy(bankIdAccountIds: List[BankIdAccountId], callContext: Option[CallContext]): Box[List[AccountHeld]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetBankAccountsHeldLegacy => InBound, OutBoundGetBankAccountsHeldLegacy => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankIdAccountIds)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[AccountHeld]](callContext))


  }
          
  messageDocs += getBankAccountsHeldDoc
  def getBankAccountsHeldDoc = MessageDoc(
    process = "obp.getBankAccountsHeld",
    messageFormat = messageFormat,
    description = "Get Bank Accounts Held",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetBankAccountsHeld(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankIdAccountIds=List( BankIdAccountId(bankId=BankId(bankIdExample.value),
      accountId=AccountId(accountIdExample.value))))
    ),
    exampleInboundMessage = (
     InBoundGetBankAccountsHeld(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( AccountHeld(id="string",
      bankId=bankIdExample.value,
      number="string",
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)))))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getBankAccountsHeld(bankIdAccountIds: List[BankIdAccountId], callContext: Option[CallContext]): OBPReturnType[Box[List[AccountHeld]]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetBankAccountsHeld => InBound, OutBoundGetBankAccountsHeld => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankIdAccountIds)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[AccountHeld]](callContext))


  }
          
  messageDocs += checkBankAccountExistsLegacyDoc
  def checkBankAccountExistsLegacyDoc = MessageDoc(
    process = "obp.checkBankAccountExistsLegacy",
    messageFormat = messageFormat,
    description = "Check Bank Account Exists Legacy",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundCheckBankAccountExistsLegacy(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      accountId=AccountId(accountIdExample.value))
    ),
    exampleInboundMessage = (
     InBoundCheckBankAccountExistsLegacy(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=bankAccountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def checkBankAccountExistsLegacy(bankId: BankId, accountId: AccountId, callContext: Option[CallContext]): Box[(BankAccount, Option[CallContext])] = {
    

        import com.openbankproject.commons.dto.{InBoundCheckBankAccountExistsLegacy => InBound, OutBoundCheckBankAccountExistsLegacy => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, accountId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[BankAccountCommons](callContext))


  }
          
  messageDocs += checkBankAccountExistsDoc
  def checkBankAccountExistsDoc = MessageDoc(
    process = "obp.checkBankAccountExists",
    messageFormat = messageFormat,
    description = "Check Bank Account Exists",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundCheckBankAccountExists(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      accountId=AccountId(accountIdExample.value))
    ),
    exampleInboundMessage = (
     InBoundCheckBankAccountExists(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=bankAccountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def checkBankAccountExists(bankId: BankId, accountId: AccountId, callContext: Option[CallContext]): OBPReturnType[Box[BankAccount]] = {
    

        import com.openbankproject.commons.dto.{InBoundCheckBankAccountExists => InBound, OutBoundCheckBankAccountExists => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, accountId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[BankAccountCommons](callContext))


  }
          
  messageDocs += getCounterpartyTraitDoc
  def getCounterpartyTraitDoc = MessageDoc(
    process = "obp.getCounterpartyTrait",
    messageFormat = messageFormat,
    description = "Get Counterparty Trait",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetCounterpartyTrait(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      accountId=AccountId(accountIdExample.value),
      couterpartyId="string")
    ),
    exampleInboundMessage = (
     InBoundGetCounterpartyTrait(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= CounterpartyTraitCommons(createdByUserId="string",
      name="string",
      description="string",
      thisBankId="string",
      thisAccountId="string",
      thisViewId="string",
      counterpartyId=counterpartyIdExample.value,
      otherAccountRoutingScheme=accountRoutingSchemeExample.value,
      otherAccountRoutingAddress=accountRoutingAddressExample.value,
      otherAccountSecondaryRoutingScheme="string",
      otherAccountSecondaryRoutingAddress="string",
      otherBankRoutingScheme=bankRoutingSchemeExample.value,
      otherBankRoutingAddress=bankRoutingAddressExample.value,
      otherBranchRoutingScheme=branchRoutingSchemeExample.value,
      otherBranchRoutingAddress=branchRoutingAddressExample.value,
      isBeneficiary=isBeneficiaryExample.value.toBoolean,
      bespoke=List( CounterpartyBespoke(key=keyExample.value,
      value=valueExample.value))))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getCounterpartyTrait(bankId: BankId, accountId: AccountId, couterpartyId: String, callContext: Option[CallContext]): OBPReturnType[Box[CounterpartyTrait]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetCounterpartyTrait => InBound, OutBoundGetCounterpartyTrait => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, accountId, couterpartyId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[CounterpartyTraitCommons](callContext))


  }
          
  messageDocs += getCounterpartyByCounterpartyIdLegacyDoc
  def getCounterpartyByCounterpartyIdLegacyDoc = MessageDoc(
    process = "obp.getCounterpartyByCounterpartyIdLegacy",
    messageFormat = messageFormat,
    description = "Get Counterparty By Counterparty Id Legacy",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetCounterpartyByCounterpartyIdLegacy(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      counterpartyId=CounterpartyId(counterpartyIdExample.value))
    ),
    exampleInboundMessage = (
     InBoundGetCounterpartyByCounterpartyIdLegacy(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= CounterpartyTraitCommons(createdByUserId="string",
      name="string",
      description="string",
      thisBankId="string",
      thisAccountId="string",
      thisViewId="string",
      counterpartyId=counterpartyIdExample.value,
      otherAccountRoutingScheme=accountRoutingSchemeExample.value,
      otherAccountRoutingAddress=accountRoutingAddressExample.value,
      otherAccountSecondaryRoutingScheme="string",
      otherAccountSecondaryRoutingAddress="string",
      otherBankRoutingScheme=bankRoutingSchemeExample.value,
      otherBankRoutingAddress=bankRoutingAddressExample.value,
      otherBranchRoutingScheme=branchRoutingSchemeExample.value,
      otherBranchRoutingAddress=branchRoutingAddressExample.value,
      isBeneficiary=isBeneficiaryExample.value.toBoolean,
      bespoke=List( CounterpartyBespoke(key=keyExample.value,
      value=valueExample.value))))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getCounterpartyByCounterpartyIdLegacy(counterpartyId: CounterpartyId, callContext: Option[CallContext]): Box[(CounterpartyTrait, Option[CallContext])] = {
    

        import com.openbankproject.commons.dto.{InBoundGetCounterpartyByCounterpartyIdLegacy => InBound, OutBoundGetCounterpartyByCounterpartyIdLegacy => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , counterpartyId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[CounterpartyTraitCommons](callContext))


  }
          
  messageDocs += getCounterpartyByCounterpartyIdDoc
  def getCounterpartyByCounterpartyIdDoc = MessageDoc(
    process = "obp.getCounterpartyByCounterpartyId",
    messageFormat = messageFormat,
    description = "Get Counterparty By Counterparty Id",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetCounterpartyByCounterpartyId(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      counterpartyId=CounterpartyId(counterpartyIdExample.value))
    ),
    exampleInboundMessage = (
     InBoundGetCounterpartyByCounterpartyId(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= CounterpartyTraitCommons(createdByUserId="string",
      name="string",
      description="string",
      thisBankId="string",
      thisAccountId="string",
      thisViewId="string",
      counterpartyId=counterpartyIdExample.value,
      otherAccountRoutingScheme=accountRoutingSchemeExample.value,
      otherAccountRoutingAddress=accountRoutingAddressExample.value,
      otherAccountSecondaryRoutingScheme="string",
      otherAccountSecondaryRoutingAddress="string",
      otherBankRoutingScheme=bankRoutingSchemeExample.value,
      otherBankRoutingAddress=bankRoutingAddressExample.value,
      otherBranchRoutingScheme=branchRoutingSchemeExample.value,
      otherBranchRoutingAddress=branchRoutingAddressExample.value,
      isBeneficiary=isBeneficiaryExample.value.toBoolean,
      bespoke=List( CounterpartyBespoke(key=keyExample.value,
      value=valueExample.value))))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getCounterpartyByCounterpartyId(counterpartyId: CounterpartyId, callContext: Option[CallContext]): OBPReturnType[Box[CounterpartyTrait]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetCounterpartyByCounterpartyId => InBound, OutBoundGetCounterpartyByCounterpartyId => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , counterpartyId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[CounterpartyTraitCommons](callContext))


  }
          
  messageDocs += getCounterpartyByIbanDoc
  def getCounterpartyByIbanDoc = MessageDoc(
    process = "obp.getCounterpartyByIban",
    messageFormat = messageFormat,
    description = "Get Counterparty By Iban",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetCounterpartyByIban(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      iban=ibanExample.value)
    ),
    exampleInboundMessage = (
     InBoundGetCounterpartyByIban(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= CounterpartyTraitCommons(createdByUserId="string",
      name="string",
      description="string",
      thisBankId="string",
      thisAccountId="string",
      thisViewId="string",
      counterpartyId=counterpartyIdExample.value,
      otherAccountRoutingScheme=accountRoutingSchemeExample.value,
      otherAccountRoutingAddress=accountRoutingAddressExample.value,
      otherAccountSecondaryRoutingScheme="string",
      otherAccountSecondaryRoutingAddress="string",
      otherBankRoutingScheme=bankRoutingSchemeExample.value,
      otherBankRoutingAddress=bankRoutingAddressExample.value,
      otherBranchRoutingScheme=branchRoutingSchemeExample.value,
      otherBranchRoutingAddress=branchRoutingAddressExample.value,
      isBeneficiary=isBeneficiaryExample.value.toBoolean,
      bespoke=List( CounterpartyBespoke(key=keyExample.value,
      value=valueExample.value))))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getCounterpartyByIban(iban: String, callContext: Option[CallContext]): OBPReturnType[Box[CounterpartyTrait]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetCounterpartyByIban => InBound, OutBoundGetCounterpartyByIban => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , iban)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[CounterpartyTraitCommons](callContext))


  }
          
  messageDocs += getCounterpartiesLegacyDoc
  def getCounterpartiesLegacyDoc = MessageDoc(
    process = "obp.getCounterpartiesLegacy",
    messageFormat = messageFormat,
    description = "Get Counterparties Legacy",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetCounterpartiesLegacy(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      thisBankId=BankId(bankIdExample.value),
      thisAccountId=AccountId(accountIdExample.value),
      viewId=ViewId(viewIdExample.value))
    ),
    exampleInboundMessage = (
     InBoundGetCounterpartiesLegacy(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( CounterpartyTraitCommons(createdByUserId="string",
      name="string",
      description="string",
      thisBankId="string",
      thisAccountId="string",
      thisViewId="string",
      counterpartyId=counterpartyIdExample.value,
      otherAccountRoutingScheme=accountRoutingSchemeExample.value,
      otherAccountRoutingAddress=accountRoutingAddressExample.value,
      otherAccountSecondaryRoutingScheme="string",
      otherAccountSecondaryRoutingAddress="string",
      otherBankRoutingScheme=bankRoutingSchemeExample.value,
      otherBankRoutingAddress=bankRoutingAddressExample.value,
      otherBranchRoutingScheme=branchRoutingSchemeExample.value,
      otherBranchRoutingAddress=branchRoutingAddressExample.value,
      isBeneficiary=isBeneficiaryExample.value.toBoolean,
      bespoke=List( CounterpartyBespoke(key=keyExample.value,
      value=valueExample.value)))))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getCounterpartiesLegacy(thisBankId: BankId, thisAccountId: AccountId, viewId: ViewId, callContext: Option[CallContext]): Box[(List[CounterpartyTrait], Option[CallContext])] = {
    

        import com.openbankproject.commons.dto.{InBoundGetCounterpartiesLegacy => InBound, OutBoundGetCounterpartiesLegacy => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , thisBankId, thisAccountId, viewId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[CounterpartyTraitCommons]](callContext))


  }
          
  messageDocs += getCounterpartiesDoc
  def getCounterpartiesDoc = MessageDoc(
    process = "obp.getCounterparties",
    messageFormat = messageFormat,
    description = "Get Counterparties",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetCounterparties(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      thisBankId=BankId(bankIdExample.value),
      thisAccountId=AccountId(accountIdExample.value),
      viewId=ViewId(viewIdExample.value))
    ),
    exampleInboundMessage = (
     InBoundGetCounterparties(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( CounterpartyTraitCommons(createdByUserId="string",
      name="string",
      description="string",
      thisBankId="string",
      thisAccountId="string",
      thisViewId="string",
      counterpartyId=counterpartyIdExample.value,
      otherAccountRoutingScheme=accountRoutingSchemeExample.value,
      otherAccountRoutingAddress=accountRoutingAddressExample.value,
      otherAccountSecondaryRoutingScheme="string",
      otherAccountSecondaryRoutingAddress="string",
      otherBankRoutingScheme=bankRoutingSchemeExample.value,
      otherBankRoutingAddress=bankRoutingAddressExample.value,
      otherBranchRoutingScheme=branchRoutingSchemeExample.value,
      otherBranchRoutingAddress=branchRoutingAddressExample.value,
      isBeneficiary=isBeneficiaryExample.value.toBoolean,
      bespoke=List( CounterpartyBespoke(key=keyExample.value,
      value=valueExample.value)))))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getCounterparties(thisBankId: BankId, thisAccountId: AccountId, viewId: ViewId, callContext: Option[CallContext]): OBPReturnType[Box[List[CounterpartyTrait]]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetCounterparties => InBound, OutBoundGetCounterparties => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , thisBankId, thisAccountId, viewId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[CounterpartyTraitCommons]](callContext))


  }
          
  messageDocs += getTransactionsLegacyDoc
  def getTransactionsLegacyDoc = MessageDoc(
    process = "obp.getTransactionsLegacy",
    messageFormat = messageFormat,
    description = "Get Transactions Legacy",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetTransactionsLegacy(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      accountID=AccountId(accountIdExample.value),
      limit=limitExample.value.toInt,
      offset=offsetExample.value.toInt,
      fromDate="string",
      toDate="string")
    ),
    exampleInboundMessage = (
     InBoundGetTransactionsLegacy(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( TransactionCommons(uuid=transactionUuidExample.value,
      id=TransactionId(transactionIdExample.value),
      thisAccount= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=bankAccountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value),
      otherAccount= Counterparty(nationalIdentifier=counterpartyNationalIdentifierExample.value,
      kind=counterpartyKindExample.value,
      counterpartyId=counterpartyIdExample.value,
      counterpartyName=counterpartyNameExample.value,
      thisBankId=BankId(bankIdExample.value),
      thisAccountId=AccountId(accountIdExample.value),
      otherBankRoutingScheme=counterpartyOtherBankRoutingSchemeExample.value,
      otherBankRoutingAddress=Some(counterpartyOtherBankRoutingAddressExample.value),
      otherAccountRoutingScheme=counterpartyOtherAccountRoutingSchemeExample.value,
      otherAccountRoutingAddress=Some(counterpartyOtherAccountRoutingAddressExample.value),
      otherAccountProvider=counterpartyOtherAccountProviderExample.value,
      isBeneficiary=isBeneficiaryExample.value.toBoolean),
      transactionType=transactionTypeExample.value,
      amount=BigDecimal(transactionAmountExample.value),
      currency=currencyExample.value,
      description=Some(transactionDescriptionExample.value),
      startDate=parseDate(transactionStartDateExample.value).getOrElse(sys.error("transactionStartDateExample.value is not validate date format.")),
      finishDate=parseDate(transactionFinishDateExample.value).getOrElse(sys.error("transactionFinishDateExample.value is not validate date format.")),
      balance=BigDecimal(balanceAmountExample.value))))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getTransactionsLegacy(bankId: BankId, accountID: AccountId, callContext: Option[CallContext], queryParams: List[OBPQueryParam]): Box[(List[Transaction], Option[CallContext])] = {
    

        import com.openbankproject.commons.dto.{InBoundGetTransactionsLegacy => InBound, OutBoundGetTransactionsLegacy => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, accountID, OBPQueryParam.getLimit(queryParams), OBPQueryParam.getOffset(queryParams), OBPQueryParam.getFromDate(queryParams), OBPQueryParam.getToDate(queryParams))
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[TransactionCommons]](callContext))


  }
          
  messageDocs += getTransactionsDoc
  def getTransactionsDoc = MessageDoc(
    process = "obp.getTransactions",
    messageFormat = messageFormat,
    description = "Get Transactions",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetTransactions(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      accountId=AccountId(accountIdExample.value),
      limit=limitExample.value.toInt,
      offset=offsetExample.value.toInt,
      fromDate=outBoundGetTransactionsFromDateExample.value,
      toDate=outBoundGetTransactionsToDateExample.value)
    ),
    exampleInboundMessage = (
     InBoundGetTransactions(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( TransactionCommons(uuid=transactionUuidExample.value,
      id=TransactionId(transactionIdExample.value),
      thisAccount= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=bankAccountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value),
      otherAccount= Counterparty(nationalIdentifier=counterpartyNationalIdentifierExample.value,
      kind=counterpartyKindExample.value,
      counterpartyId=counterpartyIdExample.value,
      counterpartyName=counterpartyNameExample.value,
      thisBankId=BankId(bankIdExample.value),
      thisAccountId=AccountId(accountIdExample.value),
      otherBankRoutingScheme=counterpartyOtherBankRoutingSchemeExample.value,
      otherBankRoutingAddress=Some(counterpartyOtherBankRoutingAddressExample.value),
      otherAccountRoutingScheme=counterpartyOtherAccountRoutingSchemeExample.value,
      otherAccountRoutingAddress=Some(counterpartyOtherAccountRoutingAddressExample.value),
      otherAccountProvider=counterpartyOtherAccountProviderExample.value,
      isBeneficiary=isBeneficiaryExample.value.toBoolean),
      transactionType=transactionTypeExample.value,
      amount=BigDecimal(transactionAmountExample.value),
      currency=currencyExample.value,
      description=Some(transactionDescriptionExample.value),
      startDate=parseDate(transactionStartDateExample.value).getOrElse(sys.error("transactionStartDateExample.value is not validate date format.")),
      finishDate=parseDate(transactionFinishDateExample.value).getOrElse(sys.error("transactionFinishDateExample.value is not validate date format.")),
      balance=BigDecimal(balanceAmountExample.value))))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getTransactions(bankId: BankId, accountID: AccountId, callContext: Option[CallContext], queryParams: List[OBPQueryParam]): OBPReturnType[Box[List[Transaction]]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetTransactions => InBound, OutBoundGetTransactions => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, accountID, OBPQueryParam.getLimit(queryParams), OBPQueryParam.getOffset(queryParams), OBPQueryParam.getFromDate(queryParams), OBPQueryParam.getToDate(queryParams))
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[TransactionCommons]](callContext))


  }
          
  messageDocs += getTransactionsCoreDoc
  def getTransactionsCoreDoc = MessageDoc(
    process = "obp.getTransactionsCore",
    messageFormat = messageFormat,
    description = "Get Transactions Core",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetTransactionsCore(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      accountID=AccountId(accountIdExample.value),
      limit=limitExample.value.toInt,
      offset=offsetExample.value.toInt,
      fromDate="string",
      toDate="string")
    ),
    exampleInboundMessage = (
     InBoundGetTransactionsCore(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( TransactionCore(id=TransactionId(transactionIdExample.value),
      thisAccount= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=bankAccountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value),
      otherAccount= CounterpartyCore(kind="string",
      counterpartyId=counterpartyIdExample.value,
      counterpartyName=counterpartyNameExample.value,
      thisBankId=BankId(bankIdExample.value),
      thisAccountId=AccountId(accountIdExample.value),
      otherBankRoutingScheme=bankRoutingSchemeExample.value,
      otherBankRoutingAddress=Some(bankRoutingAddressExample.value),
      otherAccountRoutingScheme=accountRoutingSchemeExample.value,
      otherAccountRoutingAddress=Some(accountRoutingAddressExample.value),
      otherAccountProvider=otherAccountProviderExample.value,
      isBeneficiary=isBeneficiaryExample.value.toBoolean),
      transactionType=transactionTypeExample.value,
      amount=BigDecimal("123.321"),
      currency=currencyExample.value,
      description=Some("string"),
      startDate=new Date(),
      finishDate=new Date(),
      balance=BigDecimal(balanceAmountExample.value))))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getTransactionsCore(bankId: BankId, accountID: AccountId, queryParams: List[OBPQueryParam], callContext: Option[CallContext]): OBPReturnType[Box[List[TransactionCore]]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetTransactionsCore => InBound, OutBoundGetTransactionsCore => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, accountID, OBPQueryParam.getLimit(queryParams), OBPQueryParam.getOffset(queryParams), OBPQueryParam.getFromDate(queryParams), OBPQueryParam.getToDate(queryParams))
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[TransactionCore]](callContext))


  }
          
  messageDocs += getTransactionLegacyDoc
  def getTransactionLegacyDoc = MessageDoc(
    process = "obp.getTransactionLegacy",
    messageFormat = messageFormat,
    description = "Get Transaction Legacy",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetTransactionLegacy(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      accountID=AccountId(accountIdExample.value),
      transactionId=TransactionId(transactionIdExample.value))
    ),
    exampleInboundMessage = (
     InBoundGetTransactionLegacy(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= TransactionCommons(uuid=transactionUuidExample.value,
      id=TransactionId(transactionIdExample.value),
      thisAccount= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=bankAccountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value),
      otherAccount= Counterparty(nationalIdentifier=counterpartyNationalIdentifierExample.value,
      kind=counterpartyKindExample.value,
      counterpartyId=counterpartyIdExample.value,
      counterpartyName=counterpartyNameExample.value,
      thisBankId=BankId(bankIdExample.value),
      thisAccountId=AccountId(accountIdExample.value),
      otherBankRoutingScheme=counterpartyOtherBankRoutingSchemeExample.value,
      otherBankRoutingAddress=Some(counterpartyOtherBankRoutingAddressExample.value),
      otherAccountRoutingScheme=counterpartyOtherAccountRoutingSchemeExample.value,
      otherAccountRoutingAddress=Some(counterpartyOtherAccountRoutingAddressExample.value),
      otherAccountProvider=counterpartyOtherAccountProviderExample.value,
      isBeneficiary=isBeneficiaryExample.value.toBoolean),
      transactionType=transactionTypeExample.value,
      amount=BigDecimal(transactionAmountExample.value),
      currency=currencyExample.value,
      description=Some(transactionDescriptionExample.value),
      startDate=parseDate(transactionStartDateExample.value).getOrElse(sys.error("transactionStartDateExample.value is not validate date format.")),
      finishDate=parseDate(transactionFinishDateExample.value).getOrElse(sys.error("transactionFinishDateExample.value is not validate date format.")),
      balance=BigDecimal(balanceAmountExample.value)))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getTransactionLegacy(bankId: BankId, accountID: AccountId, transactionId: TransactionId, callContext: Option[CallContext]): Box[(Transaction, Option[CallContext])] = {
    

        import com.openbankproject.commons.dto.{InBoundGetTransactionLegacy => InBound, OutBoundGetTransactionLegacy => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, accountID, transactionId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[TransactionCommons](callContext))


  }
          
  messageDocs += getTransactionDoc
  def getTransactionDoc = MessageDoc(
    process = "obp.getTransaction",
    messageFormat = messageFormat,
    description = "Get Transaction",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetTransaction(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      accountId=AccountId(accountIdExample.value),
      transactionId=TransactionId(transactionIdExample.value))
    ),
    exampleInboundMessage = (
     InBoundGetTransaction(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= TransactionCommons(uuid=transactionUuidExample.value,
      id=TransactionId(transactionIdExample.value),
      thisAccount= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=bankAccountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value),
      otherAccount= Counterparty(nationalIdentifier=counterpartyNationalIdentifierExample.value,
      kind=counterpartyKindExample.value,
      counterpartyId=counterpartyIdExample.value,
      counterpartyName=counterpartyNameExample.value,
      thisBankId=BankId(bankIdExample.value),
      thisAccountId=AccountId(accountIdExample.value),
      otherBankRoutingScheme=counterpartyOtherBankRoutingSchemeExample.value,
      otherBankRoutingAddress=Some(counterpartyOtherBankRoutingAddressExample.value),
      otherAccountRoutingScheme=counterpartyOtherAccountRoutingSchemeExample.value,
      otherAccountRoutingAddress=Some(counterpartyOtherAccountRoutingAddressExample.value),
      otherAccountProvider=counterpartyOtherAccountProviderExample.value,
      isBeneficiary=isBeneficiaryExample.value.toBoolean),
      transactionType=transactionTypeExample.value,
      amount=BigDecimal(transactionAmountExample.value),
      currency=currencyExample.value,
      description=Some(transactionDescriptionExample.value),
      startDate=parseDate(transactionStartDateExample.value).getOrElse(sys.error("transactionStartDateExample.value is not validate date format.")),
      finishDate=parseDate(transactionFinishDateExample.value).getOrElse(sys.error("transactionFinishDateExample.value is not validate date format.")),
      balance=BigDecimal(balanceAmountExample.value)))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getTransaction(bankId: BankId, accountID: AccountId, transactionId: TransactionId, callContext: Option[CallContext]): OBPReturnType[Box[Transaction]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetTransaction => InBound, OutBoundGetTransaction => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, accountID, transactionId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[TransactionCommons](callContext))


  }
          
  messageDocs += getPhysicalCardForBankDoc
  def getPhysicalCardForBankDoc = MessageDoc(
    process = "obp.getPhysicalCardForBank",
    messageFormat = messageFormat,
    description = "Get Physical Card For Bank",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetPhysicalCardForBank(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      cardId=cardIdExample.value)
    ),
    exampleInboundMessage = (
     InBoundGetPhysicalCardForBank(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= PhysicalCard(cardId=cardIdExample.value,
      bankId=bankIdExample.value,
      bankCardNumber=bankCardNumberExample.value,
      cardType=cardTypeExample.value,
      nameOnCard=nameOnCardExample.value,
      issueNumber=issueNumberExample.value,
      serialNumber=serialNumberExample.value,
      validFrom=new Date(),
      expires=new Date(),
      enabled=true,
      cancelled=true,
      onHotList=true,
      technology="string",
      networks=List("string"),
      allows=List(com.openbankproject.commons.model.CardAction.DEBIT),
      account= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=accountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value),
      replacement=Some( CardReplacementInfo(requestedDate=new Date(),
      reasonRequested=com.openbankproject.commons.model.CardReplacementReason.FIRST)),
      pinResets=List( PinResetInfo(requestedDate=new Date(),
      reasonRequested=com.openbankproject.commons.model.PinResetReason.FORGOT)),
      collected=Some(CardCollectionInfo(new Date())),
      posted=Some(CardPostedInfo(new Date())),
      customerId=customerIdExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getPhysicalCardForBank(bankId: BankId, cardId: String, callContext: Option[CallContext]): OBPReturnType[Box[PhysicalCardTrait]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetPhysicalCardForBank => InBound, OutBoundGetPhysicalCardForBank => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, cardId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[PhysicalCard](callContext))


  }
          
  messageDocs += deletePhysicalCardForBankDoc
  def deletePhysicalCardForBankDoc = MessageDoc(
    process = "obp.deletePhysicalCardForBank",
    messageFormat = messageFormat,
    description = "Delete Physical Card For Bank",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundDeletePhysicalCardForBank(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      cardId=cardIdExample.value)
    ),
    exampleInboundMessage = (
     InBoundDeletePhysicalCardForBank(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=true)
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def deletePhysicalCardForBank(bankId: BankId, cardId: String, callContext: Option[CallContext]): OBPReturnType[Box[Boolean]] = {
    

        import com.openbankproject.commons.dto.{InBoundDeletePhysicalCardForBank => InBound, OutBoundDeletePhysicalCardForBank => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, cardId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[Boolean](callContext))


  }
          
  messageDocs += getPhysicalCardsForBankDoc
  def getPhysicalCardsForBankDoc = MessageDoc(
    process = "obp.getPhysicalCardsForBank",
    messageFormat = messageFormat,
    description = "Get Physical Cards For Bank",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetPhysicalCardsForBank(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bank= BankCommons(bankId=BankId(bankIdExample.value),
      shortName=bankShortNameExample.value,
      fullName=bankFullNameExample.value,
      logoUrl=bankLogoUrlExample.value,
      websiteUrl=bankWebsiteUrlExample.value,
      bankRoutingScheme=bankRoutingSchemeExample.value,
      bankRoutingAddress=bankRoutingAddressExample.value,
      swiftBic=bankSwiftBicExample.value,
      nationalIdentifier=bankNationalIdentifierExample.value),
      user= UserCommons(userPrimaryKey=UserPrimaryKey(123),
      userId=userIdExample.value,
      idGivenByProvider="string",
      provider="string",
      emailAddress=emailExample.value,
      name=usernameExample.value),
      limit=limitExample.value.toInt,
      offset=offsetExample.value.toInt,
      fromDate="string",
      toDate="string")
    ),
    exampleInboundMessage = (
     InBoundGetPhysicalCardsForBank(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( PhysicalCard(cardId=cardIdExample.value,
      bankId=bankIdExample.value,
      bankCardNumber=bankCardNumberExample.value,
      cardType=cardTypeExample.value,
      nameOnCard=nameOnCardExample.value,
      issueNumber=issueNumberExample.value,
      serialNumber=serialNumberExample.value,
      validFrom=new Date(),
      expires=new Date(),
      enabled=true,
      cancelled=true,
      onHotList=true,
      technology="string",
      networks=List("string"),
      allows=List(com.openbankproject.commons.model.CardAction.DEBIT),
      account= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=accountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value),
      replacement=Some( CardReplacementInfo(requestedDate=new Date(),
      reasonRequested=com.openbankproject.commons.model.CardReplacementReason.FIRST)),
      pinResets=List( PinResetInfo(requestedDate=new Date(),
      reasonRequested=com.openbankproject.commons.model.PinResetReason.FORGOT)),
      collected=Some(CardCollectionInfo(new Date())),
      posted=Some(CardPostedInfo(new Date())),
      customerId=customerIdExample.value)))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getPhysicalCardsForBank(bank: Bank, user: User, queryParams: List[OBPQueryParam], callContext: Option[CallContext]): OBPReturnType[Box[List[PhysicalCard]]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetPhysicalCardsForBank => InBound, OutBoundGetPhysicalCardsForBank => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bank, user, OBPQueryParam.getLimit(queryParams), OBPQueryParam.getOffset(queryParams), OBPQueryParam.getFromDate(queryParams), OBPQueryParam.getToDate(queryParams))
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[PhysicalCard]](callContext))


  }
          
  messageDocs += createPhysicalCardLegacyDoc
  def createPhysicalCardLegacyDoc = MessageDoc(
    process = "obp.createPhysicalCardLegacy",
    messageFormat = messageFormat,
    description = "Create Physical Card Legacy",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundCreatePhysicalCardLegacy(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankCardNumber=bankCardNumberExample.value,
      nameOnCard=nameOnCardExample.value,
      cardType=cardTypeExample.value,
      issueNumber=issueNumberExample.value,
      serialNumber=serialNumberExample.value,
      validFrom=new Date(),
      expires=new Date(),
      enabled=true,
      cancelled=true,
      onHotList=true,
      technology="string",
      networks=List("string"),
      allows=List("string"),
      accountId=accountIdExample.value,
      bankId=bankIdExample.value,
      replacement=Some( CardReplacementInfo(requestedDate=new Date(),
      reasonRequested=com.openbankproject.commons.model.CardReplacementReason.FIRST)),
      pinResets=List( PinResetInfo(requestedDate=new Date(),
      reasonRequested=com.openbankproject.commons.model.PinResetReason.FORGOT)),
      collected=Some(CardCollectionInfo(new Date())),
      posted=Some(CardPostedInfo(new Date())),
      customerId=customerIdExample.value)
    ),
    exampleInboundMessage = (
     InBoundCreatePhysicalCardLegacy(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= PhysicalCard(cardId=cardIdExample.value,
      bankId=bankIdExample.value,
      bankCardNumber=bankCardNumberExample.value,
      cardType=cardTypeExample.value,
      nameOnCard=nameOnCardExample.value,
      issueNumber=issueNumberExample.value,
      serialNumber=serialNumberExample.value,
      validFrom=new Date(),
      expires=new Date(),
      enabled=true,
      cancelled=true,
      onHotList=true,
      technology="string",
      networks=List("string"),
      allows=List(com.openbankproject.commons.model.CardAction.DEBIT),
      account= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=accountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value),
      replacement=Some( CardReplacementInfo(requestedDate=new Date(),
      reasonRequested=com.openbankproject.commons.model.CardReplacementReason.FIRST)),
      pinResets=List( PinResetInfo(requestedDate=new Date(),
      reasonRequested=com.openbankproject.commons.model.PinResetReason.FORGOT)),
      collected=Some(CardCollectionInfo(new Date())),
      posted=Some(CardPostedInfo(new Date())),
      customerId=customerIdExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def createPhysicalCardLegacy(bankCardNumber: String, nameOnCard: String, cardType: String, issueNumber: String, serialNumber: String, validFrom: Date, expires: Date, enabled: Boolean, cancelled: Boolean, onHotList: Boolean, technology: String, networks: List[String], allows: List[String], accountId: String, bankId: String, replacement: Option[CardReplacementInfo], pinResets: List[PinResetInfo], collected: Option[CardCollectionInfo], posted: Option[CardPostedInfo], customerId: String, callContext: Option[CallContext]): Box[PhysicalCard] = {
    

        import com.openbankproject.commons.dto.{InBoundCreatePhysicalCardLegacy => InBound, OutBoundCreatePhysicalCardLegacy => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankCardNumber, nameOnCard, cardType, issueNumber, serialNumber, validFrom, expires, enabled, cancelled, onHotList, technology, networks, allows, accountId, bankId, replacement, pinResets, collected, posted, customerId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[PhysicalCard](callContext))


  }
          
  messageDocs += createPhysicalCardDoc
  def createPhysicalCardDoc = MessageDoc(
    process = "obp.createPhysicalCard",
    messageFormat = messageFormat,
    description = "Create Physical Card",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundCreatePhysicalCard(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankCardNumber=bankCardNumberExample.value,
      nameOnCard=nameOnCardExample.value,
      cardType=cardTypeExample.value,
      issueNumber=issueNumberExample.value,
      serialNumber=serialNumberExample.value,
      validFrom=new Date(),
      expires=new Date(),
      enabled=true,
      cancelled=true,
      onHotList=true,
      technology="string",
      networks=List("string"),
      allows=List("string"),
      accountId=accountIdExample.value,
      bankId=bankIdExample.value,
      replacement=Some( CardReplacementInfo(requestedDate=new Date(),
      reasonRequested=com.openbankproject.commons.model.CardReplacementReason.FIRST)),
      pinResets=List( PinResetInfo(requestedDate=new Date(),
      reasonRequested=com.openbankproject.commons.model.PinResetReason.FORGOT)),
      collected=Some(CardCollectionInfo(new Date())),
      posted=Some(CardPostedInfo(new Date())),
      customerId=customerIdExample.value)
    ),
    exampleInboundMessage = (
     InBoundCreatePhysicalCard(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= PhysicalCard(cardId=cardIdExample.value,
      bankId=bankIdExample.value,
      bankCardNumber=bankCardNumberExample.value,
      cardType=cardTypeExample.value,
      nameOnCard=nameOnCardExample.value,
      issueNumber=issueNumberExample.value,
      serialNumber=serialNumberExample.value,
      validFrom=new Date(),
      expires=new Date(),
      enabled=true,
      cancelled=true,
      onHotList=true,
      technology="string",
      networks=List("string"),
      allows=List(com.openbankproject.commons.model.CardAction.DEBIT),
      account= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=accountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value),
      replacement=Some( CardReplacementInfo(requestedDate=new Date(),
      reasonRequested=com.openbankproject.commons.model.CardReplacementReason.FIRST)),
      pinResets=List( PinResetInfo(requestedDate=new Date(),
      reasonRequested=com.openbankproject.commons.model.PinResetReason.FORGOT)),
      collected=Some(CardCollectionInfo(new Date())),
      posted=Some(CardPostedInfo(new Date())),
      customerId=customerIdExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def createPhysicalCard(bankCardNumber: String, nameOnCard: String, cardType: String, issueNumber: String, serialNumber: String, validFrom: Date, expires: Date, enabled: Boolean, cancelled: Boolean, onHotList: Boolean, technology: String, networks: List[String], allows: List[String], accountId: String, bankId: String, replacement: Option[CardReplacementInfo], pinResets: List[PinResetInfo], collected: Option[CardCollectionInfo], posted: Option[CardPostedInfo], customerId: String, callContext: Option[CallContext]): OBPReturnType[Box[PhysicalCard]] = {
    

        import com.openbankproject.commons.dto.{InBoundCreatePhysicalCard => InBound, OutBoundCreatePhysicalCard => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankCardNumber, nameOnCard, cardType, issueNumber, serialNumber, validFrom, expires, enabled, cancelled, onHotList, technology, networks, allows, accountId, bankId, replacement, pinResets, collected, posted, customerId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[PhysicalCard](callContext))


  }
          
  messageDocs += updatePhysicalCardDoc
  def updatePhysicalCardDoc = MessageDoc(
    process = "obp.updatePhysicalCard",
    messageFormat = messageFormat,
    description = "Update Physical Card",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundUpdatePhysicalCard(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      cardId=cardIdExample.value,
      bankCardNumber=bankCardNumberExample.value,
      nameOnCard=nameOnCardExample.value,
      cardType=cardTypeExample.value,
      issueNumber=issueNumberExample.value,
      serialNumber=serialNumberExample.value,
      validFrom=new Date(),
      expires=new Date(),
      enabled=true,
      cancelled=true,
      onHotList=true,
      technology="string",
      networks=List("string"),
      allows=List("string"),
      accountId=accountIdExample.value,
      bankId=bankIdExample.value,
      replacement=Some( CardReplacementInfo(requestedDate=new Date(),
      reasonRequested=com.openbankproject.commons.model.CardReplacementReason.FIRST)),
      pinResets=List( PinResetInfo(requestedDate=new Date(),
      reasonRequested=com.openbankproject.commons.model.PinResetReason.FORGOT)),
      collected=Some(CardCollectionInfo(new Date())),
      posted=Some(CardPostedInfo(new Date())),
      customerId=customerIdExample.value)
    ),
    exampleInboundMessage = (
     InBoundUpdatePhysicalCard(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= PhysicalCard(cardId=cardIdExample.value,
      bankId=bankIdExample.value,
      bankCardNumber=bankCardNumberExample.value,
      cardType=cardTypeExample.value,
      nameOnCard=nameOnCardExample.value,
      issueNumber=issueNumberExample.value,
      serialNumber=serialNumberExample.value,
      validFrom=new Date(),
      expires=new Date(),
      enabled=true,
      cancelled=true,
      onHotList=true,
      technology="string",
      networks=List("string"),
      allows=List(com.openbankproject.commons.model.CardAction.DEBIT),
      account= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=accountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value),
      replacement=Some( CardReplacementInfo(requestedDate=new Date(),
      reasonRequested=com.openbankproject.commons.model.CardReplacementReason.FIRST)),
      pinResets=List( PinResetInfo(requestedDate=new Date(),
      reasonRequested=com.openbankproject.commons.model.PinResetReason.FORGOT)),
      collected=Some(CardCollectionInfo(new Date())),
      posted=Some(CardPostedInfo(new Date())),
      customerId=customerIdExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def updatePhysicalCard(cardId: String, bankCardNumber: String, nameOnCard: String, cardType: String, issueNumber: String, serialNumber: String, validFrom: Date, expires: Date, enabled: Boolean, cancelled: Boolean, onHotList: Boolean, technology: String, networks: List[String], allows: List[String], accountId: String, bankId: String, replacement: Option[CardReplacementInfo], pinResets: List[PinResetInfo], collected: Option[CardCollectionInfo], posted: Option[CardPostedInfo], customerId: String, callContext: Option[CallContext]): OBPReturnType[Box[PhysicalCardTrait]] = {
    

        import com.openbankproject.commons.dto.{InBoundUpdatePhysicalCard => InBound, OutBoundUpdatePhysicalCard => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , cardId, bankCardNumber, nameOnCard, cardType, issueNumber, serialNumber, validFrom, expires, enabled, cancelled, onHotList, technology, networks, allows, accountId, bankId, replacement, pinResets, collected, posted, customerId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[PhysicalCard](callContext))


  }
          
  messageDocs += makePaymentv210Doc
  def makePaymentv210Doc = MessageDoc(
    process = "obp.makePaymentv210",
    messageFormat = messageFormat,
    description = "Make Paymentv210",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundMakePaymentv210(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      fromAccount= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=bankAccountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value),
      toAccount= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=bankAccountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value),
      transactionRequestCommonBody= TransactionRequestCommonBodyJSONCommons(value= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      description="string"),
      amount=BigDecimal("123.321"),
      description="string",
      transactionRequestType=TransactionRequestType(transactionRequestTypeExample.value),
      chargePolicy="string")
    ),
    exampleInboundMessage = (
     InBoundMakePaymentv210(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=TransactionId(transactionIdExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def makePaymentv210(fromAccount: BankAccount, toAccount: BankAccount, transactionRequestCommonBody: TransactionRequestCommonBodyJSON, amount: BigDecimal, description: String, transactionRequestType: TransactionRequestType, chargePolicy: String, callContext: Option[CallContext]): OBPReturnType[Box[TransactionId]] = {
    

        import com.openbankproject.commons.dto.{InBoundMakePaymentv210 => InBound, OutBoundMakePaymentv210 => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , fromAccount, toAccount, transactionRequestCommonBody, amount, description, transactionRequestType, chargePolicy)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[TransactionId](callContext))


  }
          
  messageDocs += createTransactionRequestv210Doc
  def createTransactionRequestv210Doc = MessageDoc(
    process = "obp.createTransactionRequestv210",
    messageFormat = messageFormat,
    description = "Create Transaction Requestv210",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundCreateTransactionRequestv210(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      initiator= UserCommons(userPrimaryKey=UserPrimaryKey(123),
      userId=userIdExample.value,
      idGivenByProvider="string",
      provider="string",
      emailAddress=emailExample.value,
      name=usernameExample.value),
      viewId=ViewId(viewIdExample.value),
      fromAccount= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=bankAccountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value),
      toAccount= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=bankAccountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value),
      transactionRequestType=TransactionRequestType(transactionRequestTypeExample.value),
      transactionRequestCommonBody= TransactionRequestCommonBodyJSONCommons(value= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      description="string"),
      detailsPlain="string",
      chargePolicy="string",
      challengeType=Some("string"),
      scaMethod=Some(com.openbankproject.commons.model.enums.StrongCustomerAuthentication.SMS))
    ),
    exampleInboundMessage = (
     InBoundCreateTransactionRequestv210(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= TransactionRequest(id=TransactionRequestId("string"),
      `type`=transactionRequestTypeExample.value,
      from= TransactionRequestAccount(bank_id="string",
      account_id="string"),
      body= TransactionRequestBodyAllTypes(to_sandbox_tan=Some( TransactionRequestAccount(bank_id="string",
      account_id="string")),
      to_sepa=Some(TransactionRequestIban("string")),
      to_counterparty=Some(TransactionRequestCounterpartyId("string")),
      to_transfer_to_phone=Some( TransactionRequestTransferToPhone(value= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      description="string",
      message="string",
      from= FromAccountTransfer(mobile_phone_number="string",
      nickname="string"),
      to=ToAccountTransferToPhone("string"))),
      to_transfer_to_atm=Some( TransactionRequestTransferToAtm(value= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      description="string",
      message="string",
      from= FromAccountTransfer(mobile_phone_number="string",
      nickname="string"),
      to= ToAccountTransferToAtm(legal_name="string",
      date_of_birth="string",
      mobile_phone_number="string",
      kyc_document= ToAccountTransferToAtmKycDocument(`type`="string",
      number="string")))),
      to_transfer_to_account=Some( TransactionRequestTransferToAccount(value= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      description="string",
      transfer_type="string",
      future_date="string",
      to= ToAccountTransferToAccount(name="string",
      bank_code="string",
      branch_number="string",
      account= ToAccountTransferToAccountAccount(number=accountNumberExample.value,
      iban=ibanExample.value)))),
      to_sepa_credit_transfers=Some( SepaCreditTransfers(debtorAccount=PaymentAccount("string"),
      instructedAmount= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      creditorAccount=PaymentAccount("string"),
      creditorName="string")),
      value= AmountOfMoney(currency=currencyExample.value,
      amount="string"),
      description="string"),
      transaction_ids="string",
      status="string",
      start_date=new Date(),
      end_date=new Date(),
      challenge= TransactionRequestChallenge(id="string",
      allowed_attempts=123,
      challenge_type="string"),
      charge= TransactionRequestCharge(summary="string",
      value= AmountOfMoney(currency=currencyExample.value,
      amount="string")),
      charge_policy="string",
      counterparty_id=CounterpartyId(counterpartyIdExample.value),
      name="string",
      this_bank_id=BankId(bankIdExample.value),
      this_account_id=AccountId(accountIdExample.value),
      this_view_id=ViewId(viewIdExample.value),
      other_account_routing_scheme="string",
      other_account_routing_address="string",
      other_bank_routing_scheme="string",
      other_bank_routing_address="string",
      is_beneficiary=true,
      future_date=Some("string")))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def createTransactionRequestv210(initiator: User, viewId: ViewId, fromAccount: BankAccount, toAccount: BankAccount, transactionRequestType: TransactionRequestType, transactionRequestCommonBody: TransactionRequestCommonBodyJSON, detailsPlain: String, chargePolicy: String, challengeType: Option[String], scaMethod: Option[SCA], callContext: Option[CallContext]): OBPReturnType[Box[TransactionRequest]] = {
    

        import com.openbankproject.commons.dto.{InBoundCreateTransactionRequestv210 => InBound, OutBoundCreateTransactionRequestv210 => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , initiator, viewId, fromAccount, toAccount, transactionRequestType, transactionRequestCommonBody, detailsPlain, chargePolicy, challengeType, scaMethod)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[TransactionRequest](callContext))


  }
          
  messageDocs += getTransactionRequests210Doc
  def getTransactionRequests210Doc = MessageDoc(
    process = "obp.getTransactionRequests210",
    messageFormat = messageFormat,
    description = "Get Transaction Requests210",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetTransactionRequests210(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      initiator= UserCommons(userPrimaryKey=UserPrimaryKey(123),
      userId=userIdExample.value,
      idGivenByProvider="string",
      provider="string",
      emailAddress=emailExample.value,
      name=usernameExample.value),
      fromAccount= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=bankAccountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value))
    ),
    exampleInboundMessage = (
     InBoundGetTransactionRequests210(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( TransactionRequest(id=TransactionRequestId("string"),
      `type`=transactionRequestTypeExample.value,
      from= TransactionRequestAccount(bank_id="string",
      account_id="string"),
      body= TransactionRequestBodyAllTypes(to_sandbox_tan=Some( TransactionRequestAccount(bank_id="string",
      account_id="string")),
      to_sepa=Some(TransactionRequestIban("string")),
      to_counterparty=Some(TransactionRequestCounterpartyId("string")),
      to_transfer_to_phone=Some( TransactionRequestTransferToPhone(value= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      description="string",
      message="string",
      from= FromAccountTransfer(mobile_phone_number="string",
      nickname="string"),
      to=ToAccountTransferToPhone("string"))),
      to_transfer_to_atm=Some( TransactionRequestTransferToAtm(value= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      description="string",
      message="string",
      from= FromAccountTransfer(mobile_phone_number="string",
      nickname="string"),
      to= ToAccountTransferToAtm(legal_name="string",
      date_of_birth="string",
      mobile_phone_number="string",
      kyc_document= ToAccountTransferToAtmKycDocument(`type`="string",
      number="string")))),
      to_transfer_to_account=Some( TransactionRequestTransferToAccount(value= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      description="string",
      transfer_type="string",
      future_date="string",
      to= ToAccountTransferToAccount(name="string",
      bank_code="string",
      branch_number="string",
      account= ToAccountTransferToAccountAccount(number=accountNumberExample.value,
      iban=ibanExample.value)))),
      to_sepa_credit_transfers=Some( SepaCreditTransfers(debtorAccount=PaymentAccount("string"),
      instructedAmount= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      creditorAccount=PaymentAccount("string"),
      creditorName="string")),
      value= AmountOfMoney(currency=currencyExample.value,
      amount="string"),
      description="string"),
      transaction_ids="string",
      status="string",
      start_date=new Date(),
      end_date=new Date(),
      challenge= TransactionRequestChallenge(id="string",
      allowed_attempts=123,
      challenge_type="string"),
      charge= TransactionRequestCharge(summary="string",
      value= AmountOfMoney(currency=currencyExample.value,
      amount="string")),
      charge_policy="string",
      counterparty_id=CounterpartyId(counterpartyIdExample.value),
      name="string",
      this_bank_id=BankId(bankIdExample.value),
      this_account_id=AccountId(accountIdExample.value),
      this_view_id=ViewId(viewIdExample.value),
      other_account_routing_scheme="string",
      other_account_routing_address="string",
      other_bank_routing_scheme="string",
      other_bank_routing_address="string",
      is_beneficiary=true,
      future_date=Some("string"))))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getTransactionRequests210(initiator: User, fromAccount: BankAccount, callContext: Option[CallContext]): Box[(List[TransactionRequest], Option[CallContext])] = {
    

        import com.openbankproject.commons.dto.{InBoundGetTransactionRequests210 => InBound, OutBoundGetTransactionRequests210 => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , initiator, fromAccount)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[TransactionRequest]](callContext))


  }
          
  messageDocs += getTransactionRequestImplDoc
  def getTransactionRequestImplDoc = MessageDoc(
    process = "obp.getTransactionRequestImpl",
    messageFormat = messageFormat,
    description = "Get Transaction Request Impl",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetTransactionRequestImpl(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      transactionRequestId=TransactionRequestId("string"))
    ),
    exampleInboundMessage = (
     InBoundGetTransactionRequestImpl(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= TransactionRequest(id=TransactionRequestId("string"),
      `type`=transactionRequestTypeExample.value,
      from= TransactionRequestAccount(bank_id="string",
      account_id="string"),
      body= TransactionRequestBodyAllTypes(to_sandbox_tan=Some( TransactionRequestAccount(bank_id="string",
      account_id="string")),
      to_sepa=Some(TransactionRequestIban("string")),
      to_counterparty=Some(TransactionRequestCounterpartyId("string")),
      to_transfer_to_phone=Some( TransactionRequestTransferToPhone(value= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      description="string",
      message="string",
      from= FromAccountTransfer(mobile_phone_number="string",
      nickname="string"),
      to=ToAccountTransferToPhone("string"))),
      to_transfer_to_atm=Some( TransactionRequestTransferToAtm(value= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      description="string",
      message="string",
      from= FromAccountTransfer(mobile_phone_number="string",
      nickname="string"),
      to= ToAccountTransferToAtm(legal_name="string",
      date_of_birth="string",
      mobile_phone_number="string",
      kyc_document= ToAccountTransferToAtmKycDocument(`type`="string",
      number="string")))),
      to_transfer_to_account=Some( TransactionRequestTransferToAccount(value= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      description="string",
      transfer_type="string",
      future_date="string",
      to= ToAccountTransferToAccount(name="string",
      bank_code="string",
      branch_number="string",
      account= ToAccountTransferToAccountAccount(number=accountNumberExample.value,
      iban=ibanExample.value)))),
      to_sepa_credit_transfers=Some( SepaCreditTransfers(debtorAccount=PaymentAccount("string"),
      instructedAmount= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      creditorAccount=PaymentAccount("string"),
      creditorName="string")),
      value= AmountOfMoney(currency=currencyExample.value,
      amount="string"),
      description="string"),
      transaction_ids="string",
      status="string",
      start_date=new Date(),
      end_date=new Date(),
      challenge= TransactionRequestChallenge(id="string",
      allowed_attempts=123,
      challenge_type="string"),
      charge= TransactionRequestCharge(summary="string",
      value= AmountOfMoney(currency=currencyExample.value,
      amount="string")),
      charge_policy="string",
      counterparty_id=CounterpartyId(counterpartyIdExample.value),
      name="string",
      this_bank_id=BankId(bankIdExample.value),
      this_account_id=AccountId(accountIdExample.value),
      this_view_id=ViewId(viewIdExample.value),
      other_account_routing_scheme="string",
      other_account_routing_address="string",
      other_bank_routing_scheme="string",
      other_bank_routing_address="string",
      is_beneficiary=true,
      future_date=Some("string")))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getTransactionRequestImpl(transactionRequestId: TransactionRequestId, callContext: Option[CallContext]): Box[(TransactionRequest, Option[CallContext])] = {
    

        import com.openbankproject.commons.dto.{InBoundGetTransactionRequestImpl => InBound, OutBoundGetTransactionRequestImpl => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , transactionRequestId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[TransactionRequest](callContext))


  }
          
  messageDocs += createTransactionAfterChallengeV210Doc
  def createTransactionAfterChallengeV210Doc = MessageDoc(
    process = "obp.createTransactionAfterChallengeV210",
    messageFormat = messageFormat,
    description = "Create Transaction After Challenge V210",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundCreateTransactionAfterChallengeV210(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      fromAccount= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=bankAccountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value),
      transactionRequest= TransactionRequest(id=TransactionRequestId("string"),
      `type`=transactionRequestTypeExample.value,
      from= TransactionRequestAccount(bank_id="string",
      account_id="string"),
      body= TransactionRequestBodyAllTypes(to_sandbox_tan=Some( TransactionRequestAccount(bank_id="string",
      account_id="string")),
      to_sepa=Some(TransactionRequestIban("string")),
      to_counterparty=Some(TransactionRequestCounterpartyId("string")),
      to_transfer_to_phone=Some( TransactionRequestTransferToPhone(value= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      description="string",
      message="string",
      from= FromAccountTransfer(mobile_phone_number="string",
      nickname="string"),
      to=ToAccountTransferToPhone("string"))),
      to_transfer_to_atm=Some( TransactionRequestTransferToAtm(value= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      description="string",
      message="string",
      from= FromAccountTransfer(mobile_phone_number="string",
      nickname="string"),
      to= ToAccountTransferToAtm(legal_name="string",
      date_of_birth="string",
      mobile_phone_number="string",
      kyc_document= ToAccountTransferToAtmKycDocument(`type`="string",
      number="string")))),
      to_transfer_to_account=Some( TransactionRequestTransferToAccount(value= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      description="string",
      transfer_type="string",
      future_date="string",
      to= ToAccountTransferToAccount(name="string",
      bank_code="string",
      branch_number="string",
      account= ToAccountTransferToAccountAccount(number=accountNumberExample.value,
      iban=ibanExample.value)))),
      to_sepa_credit_transfers=Some( SepaCreditTransfers(debtorAccount=PaymentAccount("string"),
      instructedAmount= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      creditorAccount=PaymentAccount("string"),
      creditorName="string")),
      value= AmountOfMoney(currency=currencyExample.value,
      amount="string"),
      description="string"),
      transaction_ids="string",
      status="string",
      start_date=new Date(),
      end_date=new Date(),
      challenge= TransactionRequestChallenge(id="string",
      allowed_attempts=123,
      challenge_type="string"),
      charge= TransactionRequestCharge(summary="string",
      value= AmountOfMoney(currency=currencyExample.value,
      amount="string")),
      charge_policy="string",
      counterparty_id=CounterpartyId(counterpartyIdExample.value),
      name="string",
      this_bank_id=BankId(bankIdExample.value),
      this_account_id=AccountId(accountIdExample.value),
      this_view_id=ViewId(viewIdExample.value),
      other_account_routing_scheme="string",
      other_account_routing_address="string",
      other_bank_routing_scheme="string",
      other_bank_routing_address="string",
      is_beneficiary=true,
      future_date=Some("string")))
    ),
    exampleInboundMessage = (
     InBoundCreateTransactionAfterChallengeV210(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= TransactionRequest(id=TransactionRequestId("string"),
      `type`=transactionRequestTypeExample.value,
      from= TransactionRequestAccount(bank_id="string",
      account_id="string"),
      body= TransactionRequestBodyAllTypes(to_sandbox_tan=Some( TransactionRequestAccount(bank_id="string",
      account_id="string")),
      to_sepa=Some(TransactionRequestIban("string")),
      to_counterparty=Some(TransactionRequestCounterpartyId("string")),
      to_transfer_to_phone=Some( TransactionRequestTransferToPhone(value= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      description="string",
      message="string",
      from= FromAccountTransfer(mobile_phone_number="string",
      nickname="string"),
      to=ToAccountTransferToPhone("string"))),
      to_transfer_to_atm=Some( TransactionRequestTransferToAtm(value= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      description="string",
      message="string",
      from= FromAccountTransfer(mobile_phone_number="string",
      nickname="string"),
      to= ToAccountTransferToAtm(legal_name="string",
      date_of_birth="string",
      mobile_phone_number="string",
      kyc_document= ToAccountTransferToAtmKycDocument(`type`="string",
      number="string")))),
      to_transfer_to_account=Some( TransactionRequestTransferToAccount(value= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      description="string",
      transfer_type="string",
      future_date="string",
      to= ToAccountTransferToAccount(name="string",
      bank_code="string",
      branch_number="string",
      account= ToAccountTransferToAccountAccount(number=accountNumberExample.value,
      iban=ibanExample.value)))),
      to_sepa_credit_transfers=Some( SepaCreditTransfers(debtorAccount=PaymentAccount("string"),
      instructedAmount= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      creditorAccount=PaymentAccount("string"),
      creditorName="string")),
      value= AmountOfMoney(currency=currencyExample.value,
      amount="string"),
      description="string"),
      transaction_ids="string",
      status="string",
      start_date=new Date(),
      end_date=new Date(),
      challenge= TransactionRequestChallenge(id="string",
      allowed_attempts=123,
      challenge_type="string"),
      charge= TransactionRequestCharge(summary="string",
      value= AmountOfMoney(currency=currencyExample.value,
      amount="string")),
      charge_policy="string",
      counterparty_id=CounterpartyId(counterpartyIdExample.value),
      name="string",
      this_bank_id=BankId(bankIdExample.value),
      this_account_id=AccountId(accountIdExample.value),
      this_view_id=ViewId(viewIdExample.value),
      other_account_routing_scheme="string",
      other_account_routing_address="string",
      other_bank_routing_scheme="string",
      other_bank_routing_address="string",
      is_beneficiary=true,
      future_date=Some("string")))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def createTransactionAfterChallengeV210(fromAccount: BankAccount, transactionRequest: TransactionRequest, callContext: Option[CallContext]): OBPReturnType[Box[TransactionRequest]] = {
    

        import com.openbankproject.commons.dto.{InBoundCreateTransactionAfterChallengeV210 => InBound, OutBoundCreateTransactionAfterChallengeV210 => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , fromAccount, transactionRequest)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[TransactionRequest](callContext))


  }
          
  messageDocs += updateBankAccountDoc
  def updateBankAccountDoc = MessageDoc(
    process = "obp.updateBankAccount",
    messageFormat = messageFormat,
    description = "Update Bank Account",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundUpdateBankAccount(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      accountLabel="string",
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value)
    ),
    exampleInboundMessage = (
     InBoundUpdateBankAccount(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=bankAccountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def updateBankAccount(bankId: BankId, accountId: AccountId, accountType: String, accountLabel: String, branchId: String, accountRoutingScheme: String, accountRoutingAddress: String, callContext: Option[CallContext]): OBPReturnType[Box[BankAccount]] = {
    

        import com.openbankproject.commons.dto.{InBoundUpdateBankAccount => InBound, OutBoundUpdateBankAccount => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, accountId, accountType, accountLabel, branchId, accountRoutingScheme, accountRoutingAddress)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[BankAccountCommons](callContext))


  }
          
  messageDocs += createBankAccountDoc
  def createBankAccountDoc = MessageDoc(
    process = "obp.createBankAccount",
    messageFormat = messageFormat,
    description = "Create Bank Account",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundCreateBankAccount(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      accountLabel="string",
      currency=currencyExample.value,
      initialBalance=BigDecimal("123.321"),
      accountHolderName="string",
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value)
    ),
    exampleInboundMessage = (
     InBoundCreateBankAccount(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=bankAccountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def createBankAccount(bankId: BankId, accountId: AccountId, accountType: String, accountLabel: String, currency: String, initialBalance: BigDecimal, accountHolderName: String, branchId: String, accountRoutingScheme: String, accountRoutingAddress: String, callContext: Option[CallContext]): OBPReturnType[Box[BankAccount]] = {
    

        import com.openbankproject.commons.dto.{InBoundCreateBankAccount => InBound, OutBoundCreateBankAccount => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, accountId, accountType, accountLabel, currency, initialBalance, accountHolderName, branchId, accountRoutingScheme, accountRoutingAddress)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[BankAccountCommons](callContext))


  }
          
  messageDocs += getBranchDoc
  def getBranchDoc = MessageDoc(
    process = "obp.getBranch",
    messageFormat = messageFormat,
    description = "Get Branch",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetBranch(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      branchId=BranchId(branchIdExample.value))
    ),
    exampleInboundMessage = (
     InBoundGetBranch(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= BranchTCommons(branchId=BranchId(branchIdExample.value),
      bankId=BankId(bankIdExample.value),
      name="string",
      address= Address(line1="string",
      line2="string",
      line3="string",
      city="string",
      county=Some("string"),
      state="string",
      postCode="string",
      countryCode="string"),
      location= Location(latitude=123.123,
      longitude=123.123,
      date=Some(new Date()),
      user=Some( BasicResourceUser(userId=userIdExample.value,
      provider="string",
      username=usernameExample.value))),
      lobbyString=Some(LobbyString("string")),
      driveUpString=Some(DriveUpString("string")),
      meta=Meta( License(id="string",
      name="string")),
      branchRouting=Some( Routing(scheme=branchRoutingSchemeExample.value,
      address=branchRoutingAddressExample.value)),
      lobby=Some( Lobby(monday=List( OpeningTimes(openingTime="string",
      closingTime="string")),
      tuesday=List( OpeningTimes(openingTime="string",
      closingTime="string")),
      wednesday=List( OpeningTimes(openingTime="string",
      closingTime="string")),
      thursday=List( OpeningTimes(openingTime="string",
      closingTime="string")),
      friday=List( OpeningTimes(openingTime="string",
      closingTime="string")),
      saturday=List( OpeningTimes(openingTime="string",
      closingTime="string")),
      sunday=List( OpeningTimes(openingTime="string",
      closingTime="string")))),
      driveUp=Some( DriveUp(monday= OpeningTimes(openingTime="string",
      closingTime="string"),
      tuesday= OpeningTimes(openingTime="string",
      closingTime="string"),
      wednesday= OpeningTimes(openingTime="string",
      closingTime="string"),
      thursday= OpeningTimes(openingTime="string",
      closingTime="string"),
      friday= OpeningTimes(openingTime="string",
      closingTime="string"),
      saturday= OpeningTimes(openingTime="string",
      closingTime="string"),
      sunday= OpeningTimes(openingTime="string",
      closingTime="string"))),
      isAccessible=Some(true),
      accessibleFeatures=Some("string"),
      branchType=Some("string"),
      moreInfo=Some("string"),
      phoneNumber=Some("string"),
      isDeleted=Some(true)))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getBranch(bankId: BankId, branchId: BranchId, callContext: Option[CallContext]): Future[Box[(BranchT, Option[CallContext])]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetBranch => InBound, OutBoundGetBranch => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, branchId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[BranchTCommons](callContext))


  }
          
  messageDocs += getBranchesDoc
  def getBranchesDoc = MessageDoc(
    process = "obp.getBranches",
    messageFormat = messageFormat,
    description = "Get Branches",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetBranches(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      limit=limitExample.value.toInt,
      offset=offsetExample.value.toInt,
      fromDate="string",
      toDate="string")
    ),
    exampleInboundMessage = (
     InBoundGetBranches(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( BranchTCommons(branchId=BranchId(branchIdExample.value),
      bankId=BankId(bankIdExample.value),
      name="string",
      address= Address(line1="string",
      line2="string",
      line3="string",
      city="string",
      county=Some("string"),
      state="string",
      postCode="string",
      countryCode="string"),
      location= Location(latitude=123.123,
      longitude=123.123,
      date=Some(new Date()),
      user=Some( BasicResourceUser(userId=userIdExample.value,
      provider="string",
      username=usernameExample.value))),
      lobbyString=Some(LobbyString("string")),
      driveUpString=Some(DriveUpString("string")),
      meta=Meta( License(id="string",
      name="string")),
      branchRouting=Some( Routing(scheme=branchRoutingSchemeExample.value,
      address=branchRoutingAddressExample.value)),
      lobby=Some( Lobby(monday=List( OpeningTimes(openingTime="string",
      closingTime="string")),
      tuesday=List( OpeningTimes(openingTime="string",
      closingTime="string")),
      wednesday=List( OpeningTimes(openingTime="string",
      closingTime="string")),
      thursday=List( OpeningTimes(openingTime="string",
      closingTime="string")),
      friday=List( OpeningTimes(openingTime="string",
      closingTime="string")),
      saturday=List( OpeningTimes(openingTime="string",
      closingTime="string")),
      sunday=List( OpeningTimes(openingTime="string",
      closingTime="string")))),
      driveUp=Some( DriveUp(monday= OpeningTimes(openingTime="string",
      closingTime="string"),
      tuesday= OpeningTimes(openingTime="string",
      closingTime="string"),
      wednesday= OpeningTimes(openingTime="string",
      closingTime="string"),
      thursday= OpeningTimes(openingTime="string",
      closingTime="string"),
      friday= OpeningTimes(openingTime="string",
      closingTime="string"),
      saturday= OpeningTimes(openingTime="string",
      closingTime="string"),
      sunday= OpeningTimes(openingTime="string",
      closingTime="string"))),
      isAccessible=Some(true),
      accessibleFeatures=Some("string"),
      branchType=Some("string"),
      moreInfo=Some("string"),
      phoneNumber=Some("string"),
      isDeleted=Some(true))))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getBranches(bankId: BankId, callContext: Option[CallContext], queryParams: List[OBPQueryParam]): Future[Box[(List[BranchT], Option[CallContext])]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetBranches => InBound, OutBoundGetBranches => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, OBPQueryParam.getLimit(queryParams), OBPQueryParam.getOffset(queryParams), OBPQueryParam.getFromDate(queryParams), OBPQueryParam.getToDate(queryParams))
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[BranchTCommons]](callContext))


  }
          
  messageDocs += getAtmDoc
  def getAtmDoc = MessageDoc(
    process = "obp.getAtm",
    messageFormat = messageFormat,
    description = "Get Atm",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetAtm(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      atmId=AtmId("string"))
    ),
    exampleInboundMessage = (
     InBoundGetAtm(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= AtmTCommons(atmId=AtmId("string"),
      bankId=BankId(bankIdExample.value),
      name="string",
      address= Address(line1="string",
      line2="string",
      line3="string",
      city="string",
      county=Some("string"),
      state="string",
      postCode="string",
      countryCode="string"),
      location= Location(latitude=123.123,
      longitude=123.123,
      date=Some(new Date()),
      user=Some( BasicResourceUser(userId=userIdExample.value,
      provider="string",
      username=usernameExample.value))),
      meta=Meta( License(id="string",
      name="string")),
      OpeningTimeOnMonday=Some("string"),
      ClosingTimeOnMonday=Some("string"),
      OpeningTimeOnTuesday=Some("string"),
      ClosingTimeOnTuesday=Some("string"),
      OpeningTimeOnWednesday=Some("string"),
      ClosingTimeOnWednesday=Some("string"),
      OpeningTimeOnThursday=Some("string"),
      ClosingTimeOnThursday=Some("string"),
      OpeningTimeOnFriday=Some("string"),
      ClosingTimeOnFriday=Some("string"),
      OpeningTimeOnSaturday=Some("string"),
      ClosingTimeOnSaturday=Some("string"),
      OpeningTimeOnSunday=Some("string"),
      ClosingTimeOnSunday=Some("string"),
      isAccessible=Some(true),
      locatedAt=Some("string"),
      moreInfo=Some("string"),
      hasDepositCapability=Some(true)))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getAtm(bankId: BankId, atmId: AtmId, callContext: Option[CallContext]): Future[Box[(AtmT, Option[CallContext])]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetAtm => InBound, OutBoundGetAtm => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, atmId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[AtmTCommons](callContext))


  }
          
  messageDocs += getAtmsDoc
  def getAtmsDoc = MessageDoc(
    process = "obp.getAtms",
    messageFormat = messageFormat,
    description = "Get Atms",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetAtms(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      limit=limitExample.value.toInt,
      offset=offsetExample.value.toInt,
      fromDate="string",
      toDate="string")
    ),
    exampleInboundMessage = (
     InBoundGetAtms(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( AtmTCommons(atmId=AtmId("string"),
      bankId=BankId(bankIdExample.value),
      name="string",
      address= Address(line1="string",
      line2="string",
      line3="string",
      city="string",
      county=Some("string"),
      state="string",
      postCode="string",
      countryCode="string"),
      location= Location(latitude=123.123,
      longitude=123.123,
      date=Some(new Date()),
      user=Some( BasicResourceUser(userId=userIdExample.value,
      provider="string",
      username=usernameExample.value))),
      meta=Meta( License(id="string",
      name="string")),
      OpeningTimeOnMonday=Some("string"),
      ClosingTimeOnMonday=Some("string"),
      OpeningTimeOnTuesday=Some("string"),
      ClosingTimeOnTuesday=Some("string"),
      OpeningTimeOnWednesday=Some("string"),
      ClosingTimeOnWednesday=Some("string"),
      OpeningTimeOnThursday=Some("string"),
      ClosingTimeOnThursday=Some("string"),
      OpeningTimeOnFriday=Some("string"),
      ClosingTimeOnFriday=Some("string"),
      OpeningTimeOnSaturday=Some("string"),
      ClosingTimeOnSaturday=Some("string"),
      OpeningTimeOnSunday=Some("string"),
      ClosingTimeOnSunday=Some("string"),
      isAccessible=Some(true),
      locatedAt=Some("string"),
      moreInfo=Some("string"),
      hasDepositCapability=Some(true))))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getAtms(bankId: BankId, callContext: Option[CallContext], queryParams: List[OBPQueryParam]): Future[Box[(List[AtmT], Option[CallContext])]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetAtms => InBound, OutBoundGetAtms => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, OBPQueryParam.getLimit(queryParams), OBPQueryParam.getOffset(queryParams), OBPQueryParam.getFromDate(queryParams), OBPQueryParam.getToDate(queryParams))
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[AtmTCommons]](callContext))


  }
          
  messageDocs += createTransactionAfterChallengev300Doc
  def createTransactionAfterChallengev300Doc = MessageDoc(
    process = "obp.createTransactionAfterChallengev300",
    messageFormat = messageFormat,
    description = "Create Transaction After Challengev300",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundCreateTransactionAfterChallengev300(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      initiator= UserCommons(userPrimaryKey=UserPrimaryKey(123),
      userId=userIdExample.value,
      idGivenByProvider="string",
      provider="string",
      emailAddress=emailExample.value,
      name=usernameExample.value),
      fromAccount= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=bankAccountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value),
      transReqId=TransactionRequestId("string"),
      transactionRequestType=TransactionRequestType(transactionRequestTypeExample.value))
    ),
    exampleInboundMessage = (
     InBoundCreateTransactionAfterChallengev300(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= TransactionRequest(id=TransactionRequestId("string"),
      `type`=transactionRequestTypeExample.value,
      from= TransactionRequestAccount(bank_id="string",
      account_id="string"),
      body= TransactionRequestBodyAllTypes(to_sandbox_tan=Some( TransactionRequestAccount(bank_id="string",
      account_id="string")),
      to_sepa=Some(TransactionRequestIban("string")),
      to_counterparty=Some(TransactionRequestCounterpartyId("string")),
      to_transfer_to_phone=Some( TransactionRequestTransferToPhone(value= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      description="string",
      message="string",
      from= FromAccountTransfer(mobile_phone_number="string",
      nickname="string"),
      to=ToAccountTransferToPhone("string"))),
      to_transfer_to_atm=Some( TransactionRequestTransferToAtm(value= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      description="string",
      message="string",
      from= FromAccountTransfer(mobile_phone_number="string",
      nickname="string"),
      to= ToAccountTransferToAtm(legal_name="string",
      date_of_birth="string",
      mobile_phone_number="string",
      kyc_document= ToAccountTransferToAtmKycDocument(`type`="string",
      number="string")))),
      to_transfer_to_account=Some( TransactionRequestTransferToAccount(value= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      description="string",
      transfer_type="string",
      future_date="string",
      to= ToAccountTransferToAccount(name="string",
      bank_code="string",
      branch_number="string",
      account= ToAccountTransferToAccountAccount(number=accountNumberExample.value,
      iban=ibanExample.value)))),
      to_sepa_credit_transfers=Some( SepaCreditTransfers(debtorAccount=PaymentAccount("string"),
      instructedAmount= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      creditorAccount=PaymentAccount("string"),
      creditorName="string")),
      value= AmountOfMoney(currency=currencyExample.value,
      amount="string"),
      description="string"),
      transaction_ids="string",
      status="string",
      start_date=new Date(),
      end_date=new Date(),
      challenge= TransactionRequestChallenge(id="string",
      allowed_attempts=123,
      challenge_type="string"),
      charge= TransactionRequestCharge(summary="string",
      value= AmountOfMoney(currency=currencyExample.value,
      amount="string")),
      charge_policy="string",
      counterparty_id=CounterpartyId(counterpartyIdExample.value),
      name="string",
      this_bank_id=BankId(bankIdExample.value),
      this_account_id=AccountId(accountIdExample.value),
      this_view_id=ViewId(viewIdExample.value),
      other_account_routing_scheme="string",
      other_account_routing_address="string",
      other_bank_routing_scheme="string",
      other_bank_routing_address="string",
      is_beneficiary=true,
      future_date=Some("string")))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def createTransactionAfterChallengev300(initiator: User, fromAccount: BankAccount, transReqId: TransactionRequestId, transactionRequestType: TransactionRequestType, callContext: Option[CallContext]): OBPReturnType[Box[TransactionRequest]] = {
    

        import com.openbankproject.commons.dto.{InBoundCreateTransactionAfterChallengev300 => InBound, OutBoundCreateTransactionAfterChallengev300 => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , initiator, fromAccount, transReqId, transactionRequestType)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[TransactionRequest](callContext))


  }
          
  messageDocs += makePaymentv300Doc
  def makePaymentv300Doc = MessageDoc(
    process = "obp.makePaymentv300",
    messageFormat = messageFormat,
    description = "Make Paymentv300",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundMakePaymentv300(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      initiator= UserCommons(userPrimaryKey=UserPrimaryKey(123),
      userId=userIdExample.value,
      idGivenByProvider="string",
      provider="string",
      emailAddress=emailExample.value,
      name=usernameExample.value),
      fromAccount= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=bankAccountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value),
      toAccount= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=bankAccountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value),
      toCounterparty= CounterpartyTraitCommons(createdByUserId="string",
      name="string",
      description="string",
      thisBankId="string",
      thisAccountId="string",
      thisViewId="string",
      counterpartyId=counterpartyIdExample.value,
      otherAccountRoutingScheme=accountRoutingSchemeExample.value,
      otherAccountRoutingAddress=accountRoutingAddressExample.value,
      otherAccountSecondaryRoutingScheme="string",
      otherAccountSecondaryRoutingAddress="string",
      otherBankRoutingScheme=bankRoutingSchemeExample.value,
      otherBankRoutingAddress=bankRoutingAddressExample.value,
      otherBranchRoutingScheme=branchRoutingSchemeExample.value,
      otherBranchRoutingAddress=branchRoutingAddressExample.value,
      isBeneficiary=isBeneficiaryExample.value.toBoolean,
      bespoke=List( CounterpartyBespoke(key=keyExample.value,
      value=valueExample.value))),
      transactionRequestCommonBody= TransactionRequestCommonBodyJSONCommons(value= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      description="string"),
      transactionRequestType=TransactionRequestType(transactionRequestTypeExample.value),
      chargePolicy="string")
    ),
    exampleInboundMessage = (
     InBoundMakePaymentv300(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=TransactionId(transactionIdExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def makePaymentv300(initiator: User, fromAccount: BankAccount, toAccount: BankAccount, toCounterparty: CounterpartyTrait, transactionRequestCommonBody: TransactionRequestCommonBodyJSON, transactionRequestType: TransactionRequestType, chargePolicy: String, callContext: Option[CallContext]): Future[Box[(TransactionId, Option[CallContext])]] = {
    

        import com.openbankproject.commons.dto.{InBoundMakePaymentv300 => InBound, OutBoundMakePaymentv300 => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , initiator, fromAccount, toAccount, toCounterparty, transactionRequestCommonBody, transactionRequestType, chargePolicy)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[TransactionId](callContext))


  }
          
  messageDocs += createTransactionRequestv300Doc
  def createTransactionRequestv300Doc = MessageDoc(
    process = "obp.createTransactionRequestv300",
    messageFormat = messageFormat,
    description = "Create Transaction Requestv300",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundCreateTransactionRequestv300(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      initiator= UserCommons(userPrimaryKey=UserPrimaryKey(123),
      userId=userIdExample.value,
      idGivenByProvider="string",
      provider="string",
      emailAddress=emailExample.value,
      name=usernameExample.value),
      viewId=ViewId(viewIdExample.value),
      fromAccount= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=bankAccountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value),
      toAccount= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=bankAccountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value),
      toCounterparty= CounterpartyTraitCommons(createdByUserId="string",
      name="string",
      description="string",
      thisBankId="string",
      thisAccountId="string",
      thisViewId="string",
      counterpartyId=counterpartyIdExample.value,
      otherAccountRoutingScheme=accountRoutingSchemeExample.value,
      otherAccountRoutingAddress=accountRoutingAddressExample.value,
      otherAccountSecondaryRoutingScheme="string",
      otherAccountSecondaryRoutingAddress="string",
      otherBankRoutingScheme=bankRoutingSchemeExample.value,
      otherBankRoutingAddress=bankRoutingAddressExample.value,
      otherBranchRoutingScheme=branchRoutingSchemeExample.value,
      otherBranchRoutingAddress=branchRoutingAddressExample.value,
      isBeneficiary=isBeneficiaryExample.value.toBoolean,
      bespoke=List( CounterpartyBespoke(key=keyExample.value,
      value=valueExample.value))),
      transactionRequestType=TransactionRequestType(transactionRequestTypeExample.value),
      transactionRequestCommonBody= TransactionRequestCommonBodyJSONCommons(value= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      description="string"),
      detailsPlain="string",
      chargePolicy="string")
    ),
    exampleInboundMessage = (
     InBoundCreateTransactionRequestv300(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= TransactionRequest(id=TransactionRequestId("string"),
      `type`=transactionRequestTypeExample.value,
      from= TransactionRequestAccount(bank_id="string",
      account_id="string"),
      body= TransactionRequestBodyAllTypes(to_sandbox_tan=Some( TransactionRequestAccount(bank_id="string",
      account_id="string")),
      to_sepa=Some(TransactionRequestIban("string")),
      to_counterparty=Some(TransactionRequestCounterpartyId("string")),
      to_transfer_to_phone=Some( TransactionRequestTransferToPhone(value= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      description="string",
      message="string",
      from= FromAccountTransfer(mobile_phone_number="string",
      nickname="string"),
      to=ToAccountTransferToPhone("string"))),
      to_transfer_to_atm=Some( TransactionRequestTransferToAtm(value= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      description="string",
      message="string",
      from= FromAccountTransfer(mobile_phone_number="string",
      nickname="string"),
      to= ToAccountTransferToAtm(legal_name="string",
      date_of_birth="string",
      mobile_phone_number="string",
      kyc_document= ToAccountTransferToAtmKycDocument(`type`="string",
      number="string")))),
      to_transfer_to_account=Some( TransactionRequestTransferToAccount(value= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      description="string",
      transfer_type="string",
      future_date="string",
      to= ToAccountTransferToAccount(name="string",
      bank_code="string",
      branch_number="string",
      account= ToAccountTransferToAccountAccount(number=accountNumberExample.value,
      iban=ibanExample.value)))),
      to_sepa_credit_transfers=Some( SepaCreditTransfers(debtorAccount=PaymentAccount("string"),
      instructedAmount= AmountOfMoneyJsonV121(currency=currencyExample.value,
      amount="string"),
      creditorAccount=PaymentAccount("string"),
      creditorName="string")),
      value= AmountOfMoney(currency=currencyExample.value,
      amount="string"),
      description="string"),
      transaction_ids="string",
      status="string",
      start_date=new Date(),
      end_date=new Date(),
      challenge= TransactionRequestChallenge(id="string",
      allowed_attempts=123,
      challenge_type="string"),
      charge= TransactionRequestCharge(summary="string",
      value= AmountOfMoney(currency=currencyExample.value,
      amount="string")),
      charge_policy="string",
      counterparty_id=CounterpartyId(counterpartyIdExample.value),
      name="string",
      this_bank_id=BankId(bankIdExample.value),
      this_account_id=AccountId(accountIdExample.value),
      this_view_id=ViewId(viewIdExample.value),
      other_account_routing_scheme="string",
      other_account_routing_address="string",
      other_bank_routing_scheme="string",
      other_bank_routing_address="string",
      is_beneficiary=true,
      future_date=Some("string")))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def createTransactionRequestv300(initiator: User, viewId: ViewId, fromAccount: BankAccount, toAccount: BankAccount, toCounterparty: CounterpartyTrait, transactionRequestType: TransactionRequestType, transactionRequestCommonBody: TransactionRequestCommonBodyJSON, detailsPlain: String, chargePolicy: String, callContext: Option[CallContext]): Future[Box[(TransactionRequest, Option[CallContext])]] = {
    

        import com.openbankproject.commons.dto.{InBoundCreateTransactionRequestv300 => InBound, OutBoundCreateTransactionRequestv300 => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , initiator, viewId, fromAccount, toAccount, toCounterparty, transactionRequestType, transactionRequestCommonBody, detailsPlain, chargePolicy)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[TransactionRequest](callContext))


  }
          
  messageDocs += createCounterpartyDoc
  def createCounterpartyDoc = MessageDoc(
    process = "obp.createCounterparty",
    messageFormat = messageFormat,
    description = "Create Counterparty",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundCreateCounterparty(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      name="string",
      description="string",
      createdByUserId="string",
      thisBankId="string",
      thisAccountId="string",
      thisViewId="string",
      otherAccountRoutingScheme=accountRoutingSchemeExample.value,
      otherAccountRoutingAddress=accountRoutingAddressExample.value,
      otherAccountSecondaryRoutingScheme="string",
      otherAccountSecondaryRoutingAddress="string",
      otherBankRoutingScheme=bankRoutingSchemeExample.value,
      otherBankRoutingAddress=bankRoutingAddressExample.value,
      otherBranchRoutingScheme=branchRoutingSchemeExample.value,
      otherBranchRoutingAddress=branchRoutingAddressExample.value,
      isBeneficiary=isBeneficiaryExample.value.toBoolean,
      bespoke=List( CounterpartyBespoke(key=keyExample.value,
      value=valueExample.value)))
    ),
    exampleInboundMessage = (
     InBoundCreateCounterparty(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= CounterpartyTraitCommons(createdByUserId="string",
      name="string",
      description="string",
      thisBankId="string",
      thisAccountId="string",
      thisViewId="string",
      counterpartyId=counterpartyIdExample.value,
      otherAccountRoutingScheme=accountRoutingSchemeExample.value,
      otherAccountRoutingAddress=accountRoutingAddressExample.value,
      otherAccountSecondaryRoutingScheme="string",
      otherAccountSecondaryRoutingAddress="string",
      otherBankRoutingScheme=bankRoutingSchemeExample.value,
      otherBankRoutingAddress=bankRoutingAddressExample.value,
      otherBranchRoutingScheme=branchRoutingSchemeExample.value,
      otherBranchRoutingAddress=branchRoutingAddressExample.value,
      isBeneficiary=isBeneficiaryExample.value.toBoolean,
      bespoke=List( CounterpartyBespoke(key=keyExample.value,
      value=valueExample.value))))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def createCounterparty(name: String, description: String, createdByUserId: String, thisBankId: String, thisAccountId: String, thisViewId: String, otherAccountRoutingScheme: String, otherAccountRoutingAddress: String, otherAccountSecondaryRoutingScheme: String, otherAccountSecondaryRoutingAddress: String, otherBankRoutingScheme: String, otherBankRoutingAddress: String, otherBranchRoutingScheme: String, otherBranchRoutingAddress: String, isBeneficiary: Boolean, bespoke: List[CounterpartyBespoke], callContext: Option[CallContext]): Box[(CounterpartyTrait, Option[CallContext])] = {
    

        import com.openbankproject.commons.dto.{InBoundCreateCounterparty => InBound, OutBoundCreateCounterparty => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , name, description, createdByUserId, thisBankId, thisAccountId, thisViewId, otherAccountRoutingScheme, otherAccountRoutingAddress, otherAccountSecondaryRoutingScheme, otherAccountSecondaryRoutingAddress, otherBankRoutingScheme, otherBankRoutingAddress, otherBranchRoutingScheme, otherBranchRoutingAddress, isBeneficiary, bespoke)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[CounterpartyTraitCommons](callContext))


  }
          
  messageDocs += checkCustomerNumberAvailableDoc
  def checkCustomerNumberAvailableDoc = MessageDoc(
    process = "obp.checkCustomerNumberAvailable",
    messageFormat = messageFormat,
    description = "Check Customer Number Available",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundCheckCustomerNumberAvailable(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      customerNumber=customerNumberExample.value)
    ),
    exampleInboundMessage = (
     InBoundCheckCustomerNumberAvailable(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=true)
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def checkCustomerNumberAvailable(bankId: BankId, customerNumber: String, callContext: Option[CallContext]): OBPReturnType[Box[Boolean]] = {
    

        import com.openbankproject.commons.dto.{InBoundCheckCustomerNumberAvailable => InBound, OutBoundCheckCustomerNumberAvailable => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, customerNumber)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[Boolean](callContext))


  }
          
  messageDocs += createCustomerDoc
  def createCustomerDoc = MessageDoc(
    process = "obp.createCustomer",
    messageFormat = messageFormat,
    description = "Create Customer",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundCreateCustomer(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      legalName=legalNameExample.value,
      mobileNumber=mobileNumberExample.value,
      email=emailExample.value,
      faceImage= CustomerFaceImage(date=parseDate(customerFaceImageDateExample.value).getOrElse(sys.error("customerFaceImageDateExample.value is not validate date format.")),
      url=urlExample.value),
      dateOfBirth=parseDate(dateOfBirthExample.value).getOrElse(sys.error("dateOfBirthExample.value is not validate date format.")),
      relationshipStatus=relationshipStatusExample.value,
      dependents=dependentsExample.value.toInt,
      dobOfDependents=dobOfDependentsExample.value.split("[,;]").map(parseDate).flatMap(_.toSeq).toList,
      highestEducationAttained=highestEducationAttainedExample.value,
      employmentStatus=employmentStatusExample.value,
      kycStatus=kycStatusExample.value.toBoolean,
      lastOkDate=parseDate(outBoundCreateCustomerLastOkDateExample.value).getOrElse(sys.error("outBoundCreateCustomerLastOkDateExample.value is not validate date format.")),
      creditRating=Some( CreditRating(rating=ratingExample.value,
      source=sourceExample.value)),
      creditLimit=Some( AmountOfMoney(currency=currencyExample.value,
      amount=creditLimitAmountExample.value)),
      title=titleExample.value,
      branchId=branchIdExample.value,
      nameSuffix=nameSuffixExample.value)
    ),
    exampleInboundMessage = (
     InBoundCreateCustomer(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= CustomerCommons(customerId=customerIdExample.value,
      bankId=bankIdExample.value,
      number=customerNumberExample.value,
      legalName=legalNameExample.value,
      mobileNumber=mobileNumberExample.value,
      email=emailExample.value,
      faceImage= CustomerFaceImage(date=parseDate(customerFaceImageDateExample.value).getOrElse(sys.error("customerFaceImageDateExample.value is not validate date format.")),
      url=urlExample.value),
      dateOfBirth=parseDate(dateOfBirthExample.value).getOrElse(sys.error("dateOfBirthExample.value is not validate date format.")),
      relationshipStatus=relationshipStatusExample.value,
      dependents=dependentsExample.value.toInt,
      dobOfDependents=dobOfDependentsExample.value.split("[,;]").map(parseDate).flatMap(_.toSeq).toList,
      highestEducationAttained=highestEducationAttainedExample.value,
      employmentStatus=employmentStatusExample.value,
      creditRating= CreditRating(rating=ratingExample.value,
      source=sourceExample.value),
      creditLimit= CreditLimit(currency=currencyExample.value,
      amount=creditLimitAmountExample.value),
      kycStatus=kycStatusExample.value.toBoolean,
      lastOkDate=parseDate(customerLastOkDateExample.value).getOrElse(sys.error("customerLastOkDateExample.value is not validate date format.")),
      title=customerTitleExample.value,
      branchId=branchIdExample.value,
      nameSuffix=nameSuffixExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def createCustomer(bankId: BankId, legalName: String, mobileNumber: String, email: String, faceImage: CustomerFaceImageTrait, dateOfBirth: Date, relationshipStatus: String, dependents: Int, dobOfDependents: List[Date], highestEducationAttained: String, employmentStatus: String, kycStatus: Boolean, lastOkDate: Date, creditRating: Option[CreditRatingTrait], creditLimit: Option[AmountOfMoneyTrait], title: String, branchId: String, nameSuffix: String, callContext: Option[CallContext]): OBPReturnType[Box[Customer]] = {
    

        import com.openbankproject.commons.dto.{InBoundCreateCustomer => InBound, OutBoundCreateCustomer => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, legalName, mobileNumber, email, faceImage, dateOfBirth, relationshipStatus, dependents, dobOfDependents, highestEducationAttained, employmentStatus, kycStatus, lastOkDate, creditRating, creditLimit, title, branchId, nameSuffix)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[CustomerCommons](callContext))


  }
          
  messageDocs += updateCustomerScaDataDoc
  def updateCustomerScaDataDoc = MessageDoc(
    process = "obp.updateCustomerScaData",
    messageFormat = messageFormat,
    description = "Update Customer Sca Data",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundUpdateCustomerScaData(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      customerId=customerIdExample.value,
      mobileNumber=Some(mobileNumberExample.value),
      email=Some(emailExample.value),
      customerNumber=Some(customerNumberExample.value))
    ),
    exampleInboundMessage = (
     InBoundUpdateCustomerScaData(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= CustomerCommons(customerId=customerIdExample.value,
      bankId=bankIdExample.value,
      number=customerNumberExample.value,
      legalName=legalNameExample.value,
      mobileNumber=mobileNumberExample.value,
      email=emailExample.value,
      faceImage= CustomerFaceImage(date=parseDate(customerFaceImageDateExample.value).getOrElse(sys.error("customerFaceImageDateExample.value is not validate date format.")),
      url=urlExample.value),
      dateOfBirth=parseDate(dateOfBirthExample.value).getOrElse(sys.error("dateOfBirthExample.value is not validate date format.")),
      relationshipStatus=relationshipStatusExample.value,
      dependents=dependentsExample.value.toInt,
      dobOfDependents=dobOfDependentsExample.value.split("[,;]").map(parseDate).flatMap(_.toSeq).toList,
      highestEducationAttained=highestEducationAttainedExample.value,
      employmentStatus=employmentStatusExample.value,
      creditRating= CreditRating(rating=ratingExample.value,
      source=sourceExample.value),
      creditLimit= CreditLimit(currency=currencyExample.value,
      amount=creditLimitAmountExample.value),
      kycStatus=kycStatusExample.value.toBoolean,
      lastOkDate=parseDate(customerLastOkDateExample.value).getOrElse(sys.error("customerLastOkDateExample.value is not validate date format.")),
      title=customerTitleExample.value,
      branchId=branchIdExample.value,
      nameSuffix=nameSuffixExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def updateCustomerScaData(customerId: String, mobileNumber: Option[String], email: Option[String], customerNumber: Option[String], callContext: Option[CallContext]): OBPReturnType[Box[Customer]] = {
    

        import com.openbankproject.commons.dto.{InBoundUpdateCustomerScaData => InBound, OutBoundUpdateCustomerScaData => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , customerId, mobileNumber, email, customerNumber)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[CustomerCommons](callContext))


  }
          
  messageDocs += updateCustomerCreditDataDoc
  def updateCustomerCreditDataDoc = MessageDoc(
    process = "obp.updateCustomerCreditData",
    messageFormat = messageFormat,
    description = "Update Customer Credit Data",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundUpdateCustomerCreditData(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      customerId=customerIdExample.value,
      creditRating=Some("string"),
      creditSource=Some("string"),
      creditLimit=Some( AmountOfMoney(currency=currencyExample.value,
      amount=creditLimitAmountExample.value)))
    ),
    exampleInboundMessage = (
     InBoundUpdateCustomerCreditData(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= CustomerCommons(customerId=customerIdExample.value,
      bankId=bankIdExample.value,
      number=customerNumberExample.value,
      legalName=legalNameExample.value,
      mobileNumber=mobileNumberExample.value,
      email=emailExample.value,
      faceImage= CustomerFaceImage(date=parseDate(customerFaceImageDateExample.value).getOrElse(sys.error("customerFaceImageDateExample.value is not validate date format.")),
      url=urlExample.value),
      dateOfBirth=parseDate(dateOfBirthExample.value).getOrElse(sys.error("dateOfBirthExample.value is not validate date format.")),
      relationshipStatus=relationshipStatusExample.value,
      dependents=dependentsExample.value.toInt,
      dobOfDependents=dobOfDependentsExample.value.split("[,;]").map(parseDate).flatMap(_.toSeq).toList,
      highestEducationAttained=highestEducationAttainedExample.value,
      employmentStatus=employmentStatusExample.value,
      creditRating= CreditRating(rating=ratingExample.value,
      source=sourceExample.value),
      creditLimit= CreditLimit(currency=currencyExample.value,
      amount=creditLimitAmountExample.value),
      kycStatus=kycStatusExample.value.toBoolean,
      lastOkDate=parseDate(customerLastOkDateExample.value).getOrElse(sys.error("customerLastOkDateExample.value is not validate date format.")),
      title=customerTitleExample.value,
      branchId=branchIdExample.value,
      nameSuffix=nameSuffixExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def updateCustomerCreditData(customerId: String, creditRating: Option[String], creditSource: Option[String], creditLimit: Option[AmountOfMoney], callContext: Option[CallContext]): OBPReturnType[Box[Customer]] = {
    

        import com.openbankproject.commons.dto.{InBoundUpdateCustomerCreditData => InBound, OutBoundUpdateCustomerCreditData => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , customerId, creditRating, creditSource, creditLimit)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[CustomerCommons](callContext))


  }
          
  messageDocs += updateCustomerGeneralDataDoc
  def updateCustomerGeneralDataDoc = MessageDoc(
    process = "obp.updateCustomerGeneralData",
    messageFormat = messageFormat,
    description = "Update Customer General Data",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundUpdateCustomerGeneralData(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      customerId=customerIdExample.value,
      legalName=Some(legalNameExample.value),
      faceImage=Some( CustomerFaceImage(date=new Date(),
      url=urlExample.value)),
      dateOfBirth=Some(parseDate(dateOfBirthExample.value).getOrElse(sys.error("dateOfBirthExample.value is not validate date format."))),
      relationshipStatus=Some(relationshipStatusExample.value),
      dependents=Some(dependentsExample.value.toInt),
      highestEducationAttained=Some(highestEducationAttainedExample.value),
      employmentStatus=Some(employmentStatusExample.value),
      title=Some(titleExample.value),
      branchId=Some(branchIdExample.value),
      nameSuffix=Some(nameSuffixExample.value))
    ),
    exampleInboundMessage = (
     InBoundUpdateCustomerGeneralData(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= CustomerCommons(customerId=customerIdExample.value,
      bankId=bankIdExample.value,
      number=customerNumberExample.value,
      legalName=legalNameExample.value,
      mobileNumber=mobileNumberExample.value,
      email=emailExample.value,
      faceImage= CustomerFaceImage(date=parseDate(customerFaceImageDateExample.value).getOrElse(sys.error("customerFaceImageDateExample.value is not validate date format.")),
      url=urlExample.value),
      dateOfBirth=parseDate(dateOfBirthExample.value).getOrElse(sys.error("dateOfBirthExample.value is not validate date format.")),
      relationshipStatus=relationshipStatusExample.value,
      dependents=dependentsExample.value.toInt,
      dobOfDependents=dobOfDependentsExample.value.split("[,;]").map(parseDate).flatMap(_.toSeq).toList,
      highestEducationAttained=highestEducationAttainedExample.value,
      employmentStatus=employmentStatusExample.value,
      creditRating= CreditRating(rating=ratingExample.value,
      source=sourceExample.value),
      creditLimit= CreditLimit(currency=currencyExample.value,
      amount=creditLimitAmountExample.value),
      kycStatus=kycStatusExample.value.toBoolean,
      lastOkDate=parseDate(customerLastOkDateExample.value).getOrElse(sys.error("customerLastOkDateExample.value is not validate date format.")),
      title=customerTitleExample.value,
      branchId=branchIdExample.value,
      nameSuffix=nameSuffixExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def updateCustomerGeneralData(customerId: String, legalName: Option[String], faceImage: Option[CustomerFaceImageTrait], dateOfBirth: Option[Date], relationshipStatus: Option[String], dependents: Option[Int], highestEducationAttained: Option[String], employmentStatus: Option[String], title: Option[String], branchId: Option[String], nameSuffix: Option[String], callContext: Option[CallContext]): OBPReturnType[Box[Customer]] = {
    

        import com.openbankproject.commons.dto.{InBoundUpdateCustomerGeneralData => InBound, OutBoundUpdateCustomerGeneralData => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , customerId, legalName, faceImage, dateOfBirth, relationshipStatus, dependents, highestEducationAttained, employmentStatus, title, branchId, nameSuffix)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[CustomerCommons](callContext))


  }
          
  messageDocs += getCustomersByUserIdDoc
  def getCustomersByUserIdDoc = MessageDoc(
    process = "obp.getCustomersByUserId",
    messageFormat = messageFormat,
    description = "Get Customers By User Id",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetCustomersByUserId(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      userId=userIdExample.value)
    ),
    exampleInboundMessage = (
     InBoundGetCustomersByUserId(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( CustomerCommons(customerId=customerIdExample.value,
      bankId=bankIdExample.value,
      number=customerNumberExample.value,
      legalName=legalNameExample.value,
      mobileNumber=mobileNumberExample.value,
      email=emailExample.value,
      faceImage= CustomerFaceImage(date=parseDate(customerFaceImageDateExample.value).getOrElse(sys.error("customerFaceImageDateExample.value is not validate date format.")),
      url=urlExample.value),
      dateOfBirth=parseDate(dateOfBirthExample.value).getOrElse(sys.error("dateOfBirthExample.value is not validate date format.")),
      relationshipStatus=relationshipStatusExample.value,
      dependents=dependentsExample.value.toInt,
      dobOfDependents=dobOfDependentsExample.value.split("[,;]").map(parseDate).flatMap(_.toSeq).toList,
      highestEducationAttained=highestEducationAttainedExample.value,
      employmentStatus=employmentStatusExample.value,
      creditRating= CreditRating(rating=ratingExample.value,
      source=sourceExample.value),
      creditLimit= CreditLimit(currency=currencyExample.value,
      amount=creditLimitAmountExample.value),
      kycStatus=kycStatusExample.value.toBoolean,
      lastOkDate=parseDate(customerLastOkDateExample.value).getOrElse(sys.error("customerLastOkDateExample.value is not validate date format.")),
      title=customerTitleExample.value,
      branchId=branchIdExample.value,
      nameSuffix=nameSuffixExample.value)))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getCustomersByUserId(userId: String, callContext: Option[CallContext]): Future[Box[(List[Customer], Option[CallContext])]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetCustomersByUserId => InBound, OutBoundGetCustomersByUserId => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , userId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[CustomerCommons]](callContext))


  }
          
  messageDocs += getCustomerByCustomerIdLegacyDoc
  def getCustomerByCustomerIdLegacyDoc = MessageDoc(
    process = "obp.getCustomerByCustomerIdLegacy",
    messageFormat = messageFormat,
    description = "Get Customer By Customer Id Legacy",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetCustomerByCustomerIdLegacy(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      customerId=customerIdExample.value)
    ),
    exampleInboundMessage = (
     InBoundGetCustomerByCustomerIdLegacy(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= CustomerCommons(customerId=customerIdExample.value,
      bankId=bankIdExample.value,
      number=customerNumberExample.value,
      legalName=legalNameExample.value,
      mobileNumber=mobileNumberExample.value,
      email=emailExample.value,
      faceImage= CustomerFaceImage(date=parseDate(customerFaceImageDateExample.value).getOrElse(sys.error("customerFaceImageDateExample.value is not validate date format.")),
      url=urlExample.value),
      dateOfBirth=parseDate(dateOfBirthExample.value).getOrElse(sys.error("dateOfBirthExample.value is not validate date format.")),
      relationshipStatus=relationshipStatusExample.value,
      dependents=dependentsExample.value.toInt,
      dobOfDependents=dobOfDependentsExample.value.split("[,;]").map(parseDate).flatMap(_.toSeq).toList,
      highestEducationAttained=highestEducationAttainedExample.value,
      employmentStatus=employmentStatusExample.value,
      creditRating= CreditRating(rating=ratingExample.value,
      source=sourceExample.value),
      creditLimit= CreditLimit(currency=currencyExample.value,
      amount=creditLimitAmountExample.value),
      kycStatus=kycStatusExample.value.toBoolean,
      lastOkDate=parseDate(customerLastOkDateExample.value).getOrElse(sys.error("customerLastOkDateExample.value is not validate date format.")),
      title=customerTitleExample.value,
      branchId=branchIdExample.value,
      nameSuffix=nameSuffixExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getCustomerByCustomerIdLegacy(customerId: String, callContext: Option[CallContext]): Box[(Customer, Option[CallContext])] = {
    

        import com.openbankproject.commons.dto.{InBoundGetCustomerByCustomerIdLegacy => InBound, OutBoundGetCustomerByCustomerIdLegacy => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , customerId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[CustomerCommons](callContext))


  }
          
  messageDocs += getCustomerByCustomerIdDoc
  def getCustomerByCustomerIdDoc = MessageDoc(
    process = "obp.getCustomerByCustomerId",
    messageFormat = messageFormat,
    description = "Get Customer By Customer Id",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetCustomerByCustomerId(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      customerId=customerIdExample.value)
    ),
    exampleInboundMessage = (
     InBoundGetCustomerByCustomerId(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= CustomerCommons(customerId=customerIdExample.value,
      bankId=bankIdExample.value,
      number=customerNumberExample.value,
      legalName=legalNameExample.value,
      mobileNumber=mobileNumberExample.value,
      email=emailExample.value,
      faceImage= CustomerFaceImage(date=parseDate(customerFaceImageDateExample.value).getOrElse(sys.error("customerFaceImageDateExample.value is not validate date format.")),
      url=urlExample.value),
      dateOfBirth=parseDate(dateOfBirthExample.value).getOrElse(sys.error("dateOfBirthExample.value is not validate date format.")),
      relationshipStatus=relationshipStatusExample.value,
      dependents=dependentsExample.value.toInt,
      dobOfDependents=dobOfDependentsExample.value.split("[,;]").map(parseDate).flatMap(_.toSeq).toList,
      highestEducationAttained=highestEducationAttainedExample.value,
      employmentStatus=employmentStatusExample.value,
      creditRating= CreditRating(rating=ratingExample.value,
      source=sourceExample.value),
      creditLimit= CreditLimit(currency=currencyExample.value,
      amount=creditLimitAmountExample.value),
      kycStatus=kycStatusExample.value.toBoolean,
      lastOkDate=parseDate(customerLastOkDateExample.value).getOrElse(sys.error("customerLastOkDateExample.value is not validate date format.")),
      title=customerTitleExample.value,
      branchId=branchIdExample.value,
      nameSuffix=nameSuffixExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getCustomerByCustomerId(customerId: String, callContext: Option[CallContext]): Future[Box[(Customer, Option[CallContext])]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetCustomerByCustomerId => InBound, OutBoundGetCustomerByCustomerId => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , customerId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[CustomerCommons](callContext))


  }
          
  messageDocs += getCustomerByCustomerNumberDoc
  def getCustomerByCustomerNumberDoc = MessageDoc(
    process = "obp.getCustomerByCustomerNumber",
    messageFormat = messageFormat,
    description = "Get Customer By Customer Number",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetCustomerByCustomerNumber(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      customerNumber=customerNumberExample.value,
      bankId=BankId(bankIdExample.value))
    ),
    exampleInboundMessage = (
     InBoundGetCustomerByCustomerNumber(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= CustomerCommons(customerId=customerIdExample.value,
      bankId=bankIdExample.value,
      number=customerNumberExample.value,
      legalName=legalNameExample.value,
      mobileNumber=mobileNumberExample.value,
      email=emailExample.value,
      faceImage= CustomerFaceImage(date=parseDate(customerFaceImageDateExample.value).getOrElse(sys.error("customerFaceImageDateExample.value is not validate date format.")),
      url=urlExample.value),
      dateOfBirth=parseDate(dateOfBirthExample.value).getOrElse(sys.error("dateOfBirthExample.value is not validate date format.")),
      relationshipStatus=relationshipStatusExample.value,
      dependents=dependentsExample.value.toInt,
      dobOfDependents=dobOfDependentsExample.value.split("[,;]").map(parseDate).flatMap(_.toSeq).toList,
      highestEducationAttained=highestEducationAttainedExample.value,
      employmentStatus=employmentStatusExample.value,
      creditRating= CreditRating(rating=ratingExample.value,
      source=sourceExample.value),
      creditLimit= CreditLimit(currency=currencyExample.value,
      amount=creditLimitAmountExample.value),
      kycStatus=kycStatusExample.value.toBoolean,
      lastOkDate=parseDate(customerLastOkDateExample.value).getOrElse(sys.error("customerLastOkDateExample.value is not validate date format.")),
      title=customerTitleExample.value,
      branchId=branchIdExample.value,
      nameSuffix=nameSuffixExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getCustomerByCustomerNumber(customerNumber: String, bankId: BankId, callContext: Option[CallContext]): Future[Box[(Customer, Option[CallContext])]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetCustomerByCustomerNumber => InBound, OutBoundGetCustomerByCustomerNumber => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , customerNumber, bankId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[CustomerCommons](callContext))


  }
          
  messageDocs += getCustomerAddressDoc
  def getCustomerAddressDoc = MessageDoc(
    process = "obp.getCustomerAddress",
    messageFormat = messageFormat,
    description = "Get Customer Address",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetCustomerAddress(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      customerId=customerIdExample.value)
    ),
    exampleInboundMessage = (
     InBoundGetCustomerAddress(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( CustomerAddressCommons(customerId=customerIdExample.value,
      customerAddressId="string",
      line1="string",
      line2="string",
      line3="string",
      city="string",
      county="string",
      state="string",
      postcode="string",
      countryCode="string",
      status="string",
      tags="string",
      insertDate=new Date())))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getCustomerAddress(customerId: String, callContext: Option[CallContext]): OBPReturnType[Box[List[CustomerAddress]]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetCustomerAddress => InBound, OutBoundGetCustomerAddress => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , customerId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[CustomerAddressCommons]](callContext))


  }
          
  messageDocs += createCustomerAddressDoc
  def createCustomerAddressDoc = MessageDoc(
    process = "obp.createCustomerAddress",
    messageFormat = messageFormat,
    description = "Create Customer Address",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundCreateCustomerAddress(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      customerId=customerIdExample.value,
      line1="string",
      line2="string",
      line3="string",
      city="string",
      county="string",
      state="string",
      postcode="string",
      countryCode="string",
      tags="string",
      status="string")
    ),
    exampleInboundMessage = (
     InBoundCreateCustomerAddress(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= CustomerAddressCommons(customerId=customerIdExample.value,
      customerAddressId="string",
      line1="string",
      line2="string",
      line3="string",
      city="string",
      county="string",
      state="string",
      postcode="string",
      countryCode="string",
      status="string",
      tags="string",
      insertDate=new Date()))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def createCustomerAddress(customerId: String, line1: String, line2: String, line3: String, city: String, county: String, state: String, postcode: String, countryCode: String, tags: String, status: String, callContext: Option[CallContext]): OBPReturnType[Box[CustomerAddress]] = {
    

        import com.openbankproject.commons.dto.{InBoundCreateCustomerAddress => InBound, OutBoundCreateCustomerAddress => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , customerId, line1, line2, line3, city, county, state, postcode, countryCode, tags, status)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[CustomerAddressCommons](callContext))


  }
          
  messageDocs += updateCustomerAddressDoc
  def updateCustomerAddressDoc = MessageDoc(
    process = "obp.updateCustomerAddress",
    messageFormat = messageFormat,
    description = "Update Customer Address",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundUpdateCustomerAddress(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      customerAddressId="string",
      line1="string",
      line2="string",
      line3="string",
      city="string",
      county="string",
      state="string",
      postcode="string",
      countryCode="string",
      tags="string",
      status="string")
    ),
    exampleInboundMessage = (
     InBoundUpdateCustomerAddress(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= CustomerAddressCommons(customerId=customerIdExample.value,
      customerAddressId="string",
      line1="string",
      line2="string",
      line3="string",
      city="string",
      county="string",
      state="string",
      postcode="string",
      countryCode="string",
      status="string",
      tags="string",
      insertDate=new Date()))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def updateCustomerAddress(customerAddressId: String, line1: String, line2: String, line3: String, city: String, county: String, state: String, postcode: String, countryCode: String, tags: String, status: String, callContext: Option[CallContext]): OBPReturnType[Box[CustomerAddress]] = {
    

        import com.openbankproject.commons.dto.{InBoundUpdateCustomerAddress => InBound, OutBoundUpdateCustomerAddress => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , customerAddressId, line1, line2, line3, city, county, state, postcode, countryCode, tags, status)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[CustomerAddressCommons](callContext))


  }
          
  messageDocs += deleteCustomerAddressDoc
  def deleteCustomerAddressDoc = MessageDoc(
    process = "obp.deleteCustomerAddress",
    messageFormat = messageFormat,
    description = "Delete Customer Address",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundDeleteCustomerAddress(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      customerAddressId="string")
    ),
    exampleInboundMessage = (
     InBoundDeleteCustomerAddress(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=true)
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def deleteCustomerAddress(customerAddressId: String, callContext: Option[CallContext]): OBPReturnType[Box[Boolean]] = {
    

        import com.openbankproject.commons.dto.{InBoundDeleteCustomerAddress => InBound, OutBoundDeleteCustomerAddress => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , customerAddressId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[Boolean](callContext))


  }
          
  messageDocs += createTaxResidenceDoc
  def createTaxResidenceDoc = MessageDoc(
    process = "obp.createTaxResidence",
    messageFormat = messageFormat,
    description = "Create Tax Residence",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundCreateTaxResidence(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      customerId=customerIdExample.value,
      domain="string",
      taxNumber="string")
    ),
    exampleInboundMessage = (
     InBoundCreateTaxResidence(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= TaxResidenceCommons(customerId=customerIdExample.value,
      taxResidenceId="string",
      domain="string",
      taxNumber="string"))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def createTaxResidence(customerId: String, domain: String, taxNumber: String, callContext: Option[CallContext]): OBPReturnType[Box[TaxResidence]] = {
    

        import com.openbankproject.commons.dto.{InBoundCreateTaxResidence => InBound, OutBoundCreateTaxResidence => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , customerId, domain, taxNumber)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[TaxResidenceCommons](callContext))


  }
          
  messageDocs += getTaxResidenceDoc
  def getTaxResidenceDoc = MessageDoc(
    process = "obp.getTaxResidence",
    messageFormat = messageFormat,
    description = "Get Tax Residence",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetTaxResidence(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      customerId=customerIdExample.value)
    ),
    exampleInboundMessage = (
     InBoundGetTaxResidence(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( TaxResidenceCommons(customerId=customerIdExample.value,
      taxResidenceId="string",
      domain="string",
      taxNumber="string")))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getTaxResidence(customerId: String, callContext: Option[CallContext]): OBPReturnType[Box[List[TaxResidence]]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetTaxResidence => InBound, OutBoundGetTaxResidence => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , customerId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[TaxResidenceCommons]](callContext))


  }
          
  messageDocs += deleteTaxResidenceDoc
  def deleteTaxResidenceDoc = MessageDoc(
    process = "obp.deleteTaxResidence",
    messageFormat = messageFormat,
    description = "Delete Tax Residence",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundDeleteTaxResidence(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      taxResourceId="string")
    ),
    exampleInboundMessage = (
     InBoundDeleteTaxResidence(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=true)
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def deleteTaxResidence(taxResourceId: String, callContext: Option[CallContext]): OBPReturnType[Box[Boolean]] = {
    

        import com.openbankproject.commons.dto.{InBoundDeleteTaxResidence => InBound, OutBoundDeleteTaxResidence => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , taxResourceId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[Boolean](callContext))


  }
          
  messageDocs += getCustomersDoc
  def getCustomersDoc = MessageDoc(
    process = "obp.getCustomers",
    messageFormat = messageFormat,
    description = "Get Customers",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetCustomers(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      limit=limitExample.value.toInt,
      offset=offsetExample.value.toInt,
      fromDate="string",
      toDate="string")
    ),
    exampleInboundMessage = (
     InBoundGetCustomers(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( CustomerCommons(customerId=customerIdExample.value,
      bankId=bankIdExample.value,
      number=customerNumberExample.value,
      legalName=legalNameExample.value,
      mobileNumber=mobileNumberExample.value,
      email=emailExample.value,
      faceImage= CustomerFaceImage(date=parseDate(customerFaceImageDateExample.value).getOrElse(sys.error("customerFaceImageDateExample.value is not validate date format.")),
      url=urlExample.value),
      dateOfBirth=parseDate(dateOfBirthExample.value).getOrElse(sys.error("dateOfBirthExample.value is not validate date format.")),
      relationshipStatus=relationshipStatusExample.value,
      dependents=dependentsExample.value.toInt,
      dobOfDependents=dobOfDependentsExample.value.split("[,;]").map(parseDate).flatMap(_.toSeq).toList,
      highestEducationAttained=highestEducationAttainedExample.value,
      employmentStatus=employmentStatusExample.value,
      creditRating= CreditRating(rating=ratingExample.value,
      source=sourceExample.value),
      creditLimit= CreditLimit(currency=currencyExample.value,
      amount=creditLimitAmountExample.value),
      kycStatus=kycStatusExample.value.toBoolean,
      lastOkDate=parseDate(customerLastOkDateExample.value).getOrElse(sys.error("customerLastOkDateExample.value is not validate date format.")),
      title=customerTitleExample.value,
      branchId=branchIdExample.value,
      nameSuffix=nameSuffixExample.value)))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getCustomers(bankId: BankId, callContext: Option[CallContext], queryParams: List[OBPQueryParam]): Future[Box[List[Customer]]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetCustomers => InBound, OutBoundGetCustomers => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, OBPQueryParam.getLimit(queryParams), OBPQueryParam.getOffset(queryParams), OBPQueryParam.getFromDate(queryParams), OBPQueryParam.getToDate(queryParams))
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[CustomerCommons]](callContext))


  }
          
  messageDocs += getCheckbookOrdersDoc
  def getCheckbookOrdersDoc = MessageDoc(
    process = "obp.getCheckbookOrders",
    messageFormat = messageFormat,
    description = "Get Checkbook Orders",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetCheckbookOrders(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=bankIdExample.value,
      accountId=accountIdExample.value)
    ),
    exampleInboundMessage = (
     InBoundGetCheckbookOrders(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= CheckbookOrdersJson(account= AccountV310Json(bank_id="string",
      account_id="string",
      account_type="string",
      account_routings=List( AccountRoutingJsonV121(scheme="string",
      address="string")),
      branch_routings=List( BranchRoutingJsonV141(scheme="string",
      address="string"))),
      orders=List(OrderJson( OrderObjectJson(order_id="string",
      order_date="string",
      number_of_checkbooks="string",
      distribution_channel="string",
      status="string",
      first_check_number="string",
      shipping_code="string")))))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getCheckbookOrders(bankId: String, accountId: String, callContext: Option[CallContext]): Future[Box[(CheckbookOrdersJson, Option[CallContext])]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetCheckbookOrders => InBound, OutBoundGetCheckbookOrders => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, accountId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[CheckbookOrdersJson](callContext))


  }
          
  messageDocs += getStatusOfCreditCardOrderDoc
  def getStatusOfCreditCardOrderDoc = MessageDoc(
    process = "obp.getStatusOfCreditCardOrder",
    messageFormat = messageFormat,
    description = "Get Status Of Credit Card Order",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetStatusOfCreditCardOrder(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=bankIdExample.value,
      accountId=accountIdExample.value)
    ),
    exampleInboundMessage = (
     InBoundGetStatusOfCreditCardOrder(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( CardObjectJson(card_type="string",
      card_description="string",
      use_type="string")))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getStatusOfCreditCardOrder(bankId: String, accountId: String, callContext: Option[CallContext]): Future[Box[(List[CardObjectJson], Option[CallContext])]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetStatusOfCreditCardOrder => InBound, OutBoundGetStatusOfCreditCardOrder => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, accountId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[CardObjectJson]](callContext))


  }
          
  messageDocs += createUserAuthContextDoc
  def createUserAuthContextDoc = MessageDoc(
    process = "obp.createUserAuthContext",
    messageFormat = messageFormat,
    description = "Create User Auth Context",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundCreateUserAuthContext(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      userId=userIdExample.value,
      key=keyExample.value,
      value=valueExample.value)
    ),
    exampleInboundMessage = (
     InBoundCreateUserAuthContext(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= UserAuthContextCommons(userAuthContextId="string",
      userId=userIdExample.value,
      key=keyExample.value,
      value=valueExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def createUserAuthContext(userId: String, key: String, value: String, callContext: Option[CallContext]): OBPReturnType[Box[UserAuthContext]] = {
    

        import com.openbankproject.commons.dto.{InBoundCreateUserAuthContext => InBound, OutBoundCreateUserAuthContext => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , userId, key, value)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[UserAuthContextCommons](callContext))


  }
          
  messageDocs += createUserAuthContextUpdateDoc
  def createUserAuthContextUpdateDoc = MessageDoc(
    process = "obp.createUserAuthContextUpdate",
    messageFormat = messageFormat,
    description = "Create User Auth Context Update",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundCreateUserAuthContextUpdate(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      userId=userIdExample.value,
      key=keyExample.value,
      value=valueExample.value)
    ),
    exampleInboundMessage = (
     InBoundCreateUserAuthContextUpdate(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= UserAuthContextUpdateCommons(userAuthContextUpdateId="string",
      userId=userIdExample.value,
      key=keyExample.value,
      value=valueExample.value,
      challenge="string",
      status="string"))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def createUserAuthContextUpdate(userId: String, key: String, value: String, callContext: Option[CallContext]): OBPReturnType[Box[UserAuthContextUpdate]] = {
    

        import com.openbankproject.commons.dto.{InBoundCreateUserAuthContextUpdate => InBound, OutBoundCreateUserAuthContextUpdate => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , userId, key, value)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[UserAuthContextUpdateCommons](callContext))


  }
          
  messageDocs += deleteUserAuthContextsDoc
  def deleteUserAuthContextsDoc = MessageDoc(
    process = "obp.deleteUserAuthContexts",
    messageFormat = messageFormat,
    description = "Delete User Auth Contexts",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundDeleteUserAuthContexts(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      userId=userIdExample.value)
    ),
    exampleInboundMessage = (
     InBoundDeleteUserAuthContexts(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=true)
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def deleteUserAuthContexts(userId: String, callContext: Option[CallContext]): OBPReturnType[Box[Boolean]] = {
    

        import com.openbankproject.commons.dto.{InBoundDeleteUserAuthContexts => InBound, OutBoundDeleteUserAuthContexts => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , userId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[Boolean](callContext))


  }
          
  messageDocs += deleteUserAuthContextByIdDoc
  def deleteUserAuthContextByIdDoc = MessageDoc(
    process = "obp.deleteUserAuthContextById",
    messageFormat = messageFormat,
    description = "Delete User Auth Context By Id",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundDeleteUserAuthContextById(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      userAuthContextId="string")
    ),
    exampleInboundMessage = (
     InBoundDeleteUserAuthContextById(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=true)
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def deleteUserAuthContextById(userAuthContextId: String, callContext: Option[CallContext]): OBPReturnType[Box[Boolean]] = {
    

        import com.openbankproject.commons.dto.{InBoundDeleteUserAuthContextById => InBound, OutBoundDeleteUserAuthContextById => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , userAuthContextId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[Boolean](callContext))


  }
          
  messageDocs += getUserAuthContextsDoc
  def getUserAuthContextsDoc = MessageDoc(
    process = "obp.getUserAuthContexts",
    messageFormat = messageFormat,
    description = "Get User Auth Contexts",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetUserAuthContexts(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      userId=userIdExample.value)
    ),
    exampleInboundMessage = (
     InBoundGetUserAuthContexts(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( UserAuthContextCommons(userAuthContextId="string",
      userId=userIdExample.value,
      key=keyExample.value,
      value=valueExample.value)))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getUserAuthContexts(userId: String, callContext: Option[CallContext]): OBPReturnType[Box[List[UserAuthContext]]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetUserAuthContexts => InBound, OutBoundGetUserAuthContexts => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , userId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[UserAuthContextCommons]](callContext))


  }
          
  messageDocs += createOrUpdateProductAttributeDoc
  def createOrUpdateProductAttributeDoc = MessageDoc(
    process = "obp.createOrUpdateProductAttribute",
    messageFormat = messageFormat,
    description = "Create Or Update Product Attribute",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundCreateOrUpdateProductAttribute(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      productCode=ProductCode("string"),
      productAttributeId=Some("string"),
      name="string",
      productAttributeType=com.openbankproject.commons.model.enums.ProductAttributeType.example,
      value=valueExample.value)
    ),
    exampleInboundMessage = (
     InBoundCreateOrUpdateProductAttribute(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= ProductAttributeCommons(bankId=BankId(bankIdExample.value),
      productCode=ProductCode("string"),
      productAttributeId="string",
      name="string",
      attributeType=com.openbankproject.commons.model.enums.ProductAttributeType.example,
      value=valueExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def createOrUpdateProductAttribute(bankId: BankId, productCode: ProductCode, productAttributeId: Option[String], name: String, productAttributeType: ProductAttributeType.Value, value: String, callContext: Option[CallContext]): OBPReturnType[Box[ProductAttribute]] = {
    

        import com.openbankproject.commons.dto.{InBoundCreateOrUpdateProductAttribute => InBound, OutBoundCreateOrUpdateProductAttribute => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, productCode, productAttributeId, name, productAttributeType, value)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[ProductAttributeCommons](callContext))


  }
          
  messageDocs += getProductAttributeByIdDoc
  def getProductAttributeByIdDoc = MessageDoc(
    process = "obp.getProductAttributeById",
    messageFormat = messageFormat,
    description = "Get Product Attribute By Id",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetProductAttributeById(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      productAttributeId="string")
    ),
    exampleInboundMessage = (
     InBoundGetProductAttributeById(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= ProductAttributeCommons(bankId=BankId(bankIdExample.value),
      productCode=ProductCode("string"),
      productAttributeId="string",
      name="string",
      attributeType=com.openbankproject.commons.model.enums.ProductAttributeType.example,
      value=valueExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getProductAttributeById(productAttributeId: String, callContext: Option[CallContext]): OBPReturnType[Box[ProductAttribute]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetProductAttributeById => InBound, OutBoundGetProductAttributeById => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , productAttributeId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[ProductAttributeCommons](callContext))


  }
          
  messageDocs += getProductAttributesByBankAndCodeDoc
  def getProductAttributesByBankAndCodeDoc = MessageDoc(
    process = "obp.getProductAttributesByBankAndCode",
    messageFormat = messageFormat,
    description = "Get Product Attributes By Bank And Code",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetProductAttributesByBankAndCode(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bank=BankId(bankIdExample.value),
      productCode=ProductCode("string"))
    ),
    exampleInboundMessage = (
     InBoundGetProductAttributesByBankAndCode(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( ProductAttributeCommons(bankId=BankId(bankIdExample.value),
      productCode=ProductCode("string"),
      productAttributeId="string",
      name="string",
      attributeType=com.openbankproject.commons.model.enums.ProductAttributeType.example,
      value=valueExample.value)))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getProductAttributesByBankAndCode(bank: BankId, productCode: ProductCode, callContext: Option[CallContext]): OBPReturnType[Box[List[ProductAttribute]]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetProductAttributesByBankAndCode => InBound, OutBoundGetProductAttributesByBankAndCode => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bank, productCode)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[ProductAttributeCommons]](callContext))


  }
          
  messageDocs += deleteProductAttributeDoc
  def deleteProductAttributeDoc = MessageDoc(
    process = "obp.deleteProductAttribute",
    messageFormat = messageFormat,
    description = "Delete Product Attribute",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundDeleteProductAttribute(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      productAttributeId="string")
    ),
    exampleInboundMessage = (
     InBoundDeleteProductAttribute(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=true)
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def deleteProductAttribute(productAttributeId: String, callContext: Option[CallContext]): OBPReturnType[Box[Boolean]] = {
    

        import com.openbankproject.commons.dto.{InBoundDeleteProductAttribute => InBound, OutBoundDeleteProductAttribute => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , productAttributeId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[Boolean](callContext))


  }
          
  messageDocs += getAccountAttributeByIdDoc
  def getAccountAttributeByIdDoc = MessageDoc(
    process = "obp.getAccountAttributeById",
    messageFormat = messageFormat,
    description = "Get Account Attribute By Id",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetAccountAttributeById(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      accountAttributeId="string")
    ),
    exampleInboundMessage = (
     InBoundGetAccountAttributeById(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= AccountAttributeCommons(bankId=BankId(bankIdExample.value),
      accountId=AccountId(accountIdExample.value),
      productCode=ProductCode("string"),
      accountAttributeId="string",
      name="string",
      attributeType=com.openbankproject.commons.model.enums.AccountAttributeType.example,
      value=valueExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getAccountAttributeById(accountAttributeId: String, callContext: Option[CallContext]): OBPReturnType[Box[AccountAttribute]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetAccountAttributeById => InBound, OutBoundGetAccountAttributeById => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , accountAttributeId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[AccountAttributeCommons](callContext))


  }
          
  messageDocs += createOrUpdateAccountAttributeDoc
  def createOrUpdateAccountAttributeDoc = MessageDoc(
    process = "obp.createOrUpdateAccountAttribute",
    messageFormat = messageFormat,
    description = "Create Or Update Account Attribute",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundCreateOrUpdateAccountAttribute(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      accountId=AccountId(accountIdExample.value),
      productCode=ProductCode("string"),
      productAttributeId=Some("string"),
      name="string",
      accountAttributeType=com.openbankproject.commons.model.enums.AccountAttributeType.example,
      value=valueExample.value)
    ),
    exampleInboundMessage = (
     InBoundCreateOrUpdateAccountAttribute(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= AccountAttributeCommons(bankId=BankId(bankIdExample.value),
      accountId=AccountId(accountIdExample.value),
      productCode=ProductCode("string"),
      accountAttributeId="string",
      name="string",
      attributeType=com.openbankproject.commons.model.enums.AccountAttributeType.example,
      value=valueExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def createOrUpdateAccountAttribute(bankId: BankId, accountId: AccountId, productCode: ProductCode, productAttributeId: Option[String], name: String, accountAttributeType: AccountAttributeType.Value, value: String, callContext: Option[CallContext]): OBPReturnType[Box[AccountAttribute]] = {
    

        import com.openbankproject.commons.dto.{InBoundCreateOrUpdateAccountAttribute => InBound, OutBoundCreateOrUpdateAccountAttribute => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, accountId, productCode, productAttributeId, name, accountAttributeType, value)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[AccountAttributeCommons](callContext))


  }
          
  messageDocs += createAccountAttributesDoc
  def createAccountAttributesDoc = MessageDoc(
    process = "obp.createAccountAttributes",
    messageFormat = messageFormat,
    description = "Create Account Attributes",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundCreateAccountAttributes(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      accountId=AccountId(accountIdExample.value),
      productCode=ProductCode("string"),
      accountAttributes=List( ProductAttributeCommons(bankId=BankId(bankIdExample.value),
      productCode=ProductCode("string"),
      productAttributeId="string",
      name="string",
      attributeType=com.openbankproject.commons.model.enums.ProductAttributeType.example,
      value=valueExample.value)))
    ),
    exampleInboundMessage = (
     InBoundCreateAccountAttributes(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( AccountAttributeCommons(bankId=BankId(bankIdExample.value),
      accountId=AccountId(accountIdExample.value),
      productCode=ProductCode("string"),
      accountAttributeId="string",
      name="string",
      attributeType=com.openbankproject.commons.model.enums.AccountAttributeType.example,
      value=valueExample.value)))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def createAccountAttributes(bankId: BankId, accountId: AccountId, productCode: ProductCode, accountAttributes: List[ProductAttribute], callContext: Option[CallContext]): OBPReturnType[Box[List[AccountAttribute]]] = {
    

        import com.openbankproject.commons.dto.{InBoundCreateAccountAttributes => InBound, OutBoundCreateAccountAttributes => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, accountId, productCode, accountAttributes)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[AccountAttributeCommons]](callContext))


  }
          
  messageDocs += getAccountAttributesByAccountDoc
  def getAccountAttributesByAccountDoc = MessageDoc(
    process = "obp.getAccountAttributesByAccount",
    messageFormat = messageFormat,
    description = "Get Account Attributes By Account",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetAccountAttributesByAccount(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      accountId=AccountId(accountIdExample.value))
    ),
    exampleInboundMessage = (
     InBoundGetAccountAttributesByAccount(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( AccountAttributeCommons(bankId=BankId(bankIdExample.value),
      accountId=AccountId(accountIdExample.value),
      productCode=ProductCode("string"),
      accountAttributeId="string",
      name="string",
      attributeType=com.openbankproject.commons.model.enums.AccountAttributeType.example,
      value=valueExample.value)))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getAccountAttributesByAccount(bankId: BankId, accountId: AccountId, callContext: Option[CallContext]): OBPReturnType[Box[List[AccountAttribute]]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetAccountAttributesByAccount => InBound, OutBoundGetAccountAttributesByAccount => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, accountId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[AccountAttributeCommons]](callContext))


  }
          
  messageDocs += createOrUpdateCardAttributeDoc
  def createOrUpdateCardAttributeDoc = MessageDoc(
    process = "obp.createOrUpdateCardAttribute",
    messageFormat = messageFormat,
    description = "Create Or Update Card Attribute",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundCreateOrUpdateCardAttribute(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=Some(BankId(bankIdExample.value)),
      cardId=Some(cardIdExample.value),
      cardAttributeId=Some(cardAttributeIdExample.value),
      name="string",
      cardAttributeType=com.openbankproject.commons.model.enums.CardAttributeType.example,
      value=valueExample.value)
    ),
    exampleInboundMessage = (
     InBoundCreateOrUpdateCardAttribute(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= CardAttributeCommons(bankId=Some(BankId(bankIdExample.value)),
      cardId=Some(cardIdExample.value),
      cardAttributeId=Some(cardAttributeIdExample.value),
      name=cardAttributeNameExample.value,
      attributeType=com.openbankproject.commons.model.enums.CardAttributeType.example,
      value=cardAttributeValueExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def createOrUpdateCardAttribute(bankId: Option[BankId], cardId: Option[String], cardAttributeId: Option[String], name: String, cardAttributeType: CardAttributeType.Value, value: String, callContext: Option[CallContext]): OBPReturnType[Box[CardAttribute]] = {
    

        import com.openbankproject.commons.dto.{InBoundCreateOrUpdateCardAttribute => InBound, OutBoundCreateOrUpdateCardAttribute => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, cardId, cardAttributeId, name, cardAttributeType, value)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[CardAttributeCommons](callContext))


  }
          
  messageDocs += getCardAttributeByIdDoc
  def getCardAttributeByIdDoc = MessageDoc(
    process = "obp.getCardAttributeById",
    messageFormat = messageFormat,
    description = "Get Card Attribute By Id",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetCardAttributeById(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      cardAttributeId=cardAttributeIdExample.value)
    ),
    exampleInboundMessage = (
     InBoundGetCardAttributeById(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= CardAttributeCommons(bankId=Some(BankId(bankIdExample.value)),
      cardId=Some(cardIdExample.value),
      cardAttributeId=Some(cardAttributeIdExample.value),
      name=cardAttributeNameExample.value,
      attributeType=com.openbankproject.commons.model.enums.CardAttributeType.example,
      value=cardAttributeValueExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getCardAttributeById(cardAttributeId: String, callContext: Option[CallContext]): OBPReturnType[Box[CardAttribute]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetCardAttributeById => InBound, OutBoundGetCardAttributeById => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , cardAttributeId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[CardAttributeCommons](callContext))


  }
          
  messageDocs += getCardAttributesFromProviderDoc
  def getCardAttributesFromProviderDoc = MessageDoc(
    process = "obp.getCardAttributesFromProvider",
    messageFormat = messageFormat,
    description = "Get Card Attributes From Provider",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetCardAttributesFromProvider(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      cardId=cardIdExample.value)
    ),
    exampleInboundMessage = (
     InBoundGetCardAttributesFromProvider(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( CardAttributeCommons(bankId=Some(BankId(bankIdExample.value)),
      cardId=Some(cardIdExample.value),
      cardAttributeId=Some(cardAttributeIdExample.value),
      name=cardAttributeNameExample.value,
      attributeType=com.openbankproject.commons.model.enums.CardAttributeType.example,
      value=cardAttributeValueExample.value)))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getCardAttributesFromProvider(cardId: String, callContext: Option[CallContext]): OBPReturnType[Box[List[CardAttribute]]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetCardAttributesFromProvider => InBound, OutBoundGetCardAttributesFromProvider => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , cardId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[CardAttributeCommons]](callContext))


  }
          
  messageDocs += createAccountApplicationDoc
  def createAccountApplicationDoc = MessageDoc(
    process = "obp.createAccountApplication",
    messageFormat = messageFormat,
    description = "Create Account Application",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundCreateAccountApplication(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      productCode=ProductCode("string"),
      userId=Some(userIdExample.value),
      customerId=Some(customerIdExample.value))
    ),
    exampleInboundMessage = (
     InBoundCreateAccountApplication(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= AccountApplicationCommons(accountApplicationId="string",
      productCode=ProductCode("string"),
      userId=userIdExample.value,
      customerId=customerIdExample.value,
      dateOfApplication=new Date(),
      status="string"))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def createAccountApplication(productCode: ProductCode, userId: Option[String], customerId: Option[String], callContext: Option[CallContext]): OBPReturnType[Box[AccountApplication]] = {
    

        import com.openbankproject.commons.dto.{InBoundCreateAccountApplication => InBound, OutBoundCreateAccountApplication => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , productCode, userId, customerId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[AccountApplicationCommons](callContext))


  }
          
  messageDocs += getAllAccountApplicationDoc
  def getAllAccountApplicationDoc = MessageDoc(
    process = "obp.getAllAccountApplication",
    messageFormat = messageFormat,
    description = "Get All Account Application",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
          OutBoundGetAllAccountApplication(MessageDocsSwaggerDefinitions.outboundAdapterCallContext)
    ),
    exampleInboundMessage = (
     InBoundGetAllAccountApplication(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( AccountApplicationCommons(accountApplicationId="string",
      productCode=ProductCode("string"),
      userId=userIdExample.value,
      customerId=customerIdExample.value,
      dateOfApplication=new Date(),
      status="string")))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getAllAccountApplication(callContext: Option[CallContext]): OBPReturnType[Box[List[AccountApplication]]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetAllAccountApplication => InBound, OutBoundGetAllAccountApplication => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull )
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[AccountApplicationCommons]](callContext))


  }
          
  messageDocs += getAccountApplicationByIdDoc
  def getAccountApplicationByIdDoc = MessageDoc(
    process = "obp.getAccountApplicationById",
    messageFormat = messageFormat,
    description = "Get Account Application By Id",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetAccountApplicationById(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      accountApplicationId="string")
    ),
    exampleInboundMessage = (
     InBoundGetAccountApplicationById(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= AccountApplicationCommons(accountApplicationId="string",
      productCode=ProductCode("string"),
      userId=userIdExample.value,
      customerId=customerIdExample.value,
      dateOfApplication=new Date(),
      status="string"))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getAccountApplicationById(accountApplicationId: String, callContext: Option[CallContext]): OBPReturnType[Box[AccountApplication]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetAccountApplicationById => InBound, OutBoundGetAccountApplicationById => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , accountApplicationId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[AccountApplicationCommons](callContext))


  }
          
  messageDocs += updateAccountApplicationStatusDoc
  def updateAccountApplicationStatusDoc = MessageDoc(
    process = "obp.updateAccountApplicationStatus",
    messageFormat = messageFormat,
    description = "Update Account Application Status",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundUpdateAccountApplicationStatus(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      accountApplicationId="string",
      status="string")
    ),
    exampleInboundMessage = (
     InBoundUpdateAccountApplicationStatus(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= AccountApplicationCommons(accountApplicationId="string",
      productCode=ProductCode("string"),
      userId=userIdExample.value,
      customerId=customerIdExample.value,
      dateOfApplication=new Date(),
      status="string"))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def updateAccountApplicationStatus(accountApplicationId: String, status: String, callContext: Option[CallContext]): OBPReturnType[Box[AccountApplication]] = {
    

        import com.openbankproject.commons.dto.{InBoundUpdateAccountApplicationStatus => InBound, OutBoundUpdateAccountApplicationStatus => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , accountApplicationId, status)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[AccountApplicationCommons](callContext))


  }
          
  messageDocs += getOrCreateProductCollectionDoc
  def getOrCreateProductCollectionDoc = MessageDoc(
    process = "obp.getOrCreateProductCollection",
    messageFormat = messageFormat,
    description = "Get Or Create Product Collection",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetOrCreateProductCollection(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      collectionCode="string",
      productCodes=List("string"))
    ),
    exampleInboundMessage = (
     InBoundGetOrCreateProductCollection(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( ProductCollectionCommons(collectionCode="string",
      productCode="string")))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getOrCreateProductCollection(collectionCode: String, productCodes: List[String], callContext: Option[CallContext]): OBPReturnType[Box[List[ProductCollection]]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetOrCreateProductCollection => InBound, OutBoundGetOrCreateProductCollection => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , collectionCode, productCodes)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[ProductCollectionCommons]](callContext))


  }
          
  messageDocs += getProductCollectionDoc
  def getProductCollectionDoc = MessageDoc(
    process = "obp.getProductCollection",
    messageFormat = messageFormat,
    description = "Get Product Collection",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetProductCollection(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      collectionCode="string")
    ),
    exampleInboundMessage = (
     InBoundGetProductCollection(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( ProductCollectionCommons(collectionCode="string",
      productCode="string")))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getProductCollection(collectionCode: String, callContext: Option[CallContext]): OBPReturnType[Box[List[ProductCollection]]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetProductCollection => InBound, OutBoundGetProductCollection => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , collectionCode)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[ProductCollectionCommons]](callContext))


  }
          
  messageDocs += getOrCreateProductCollectionItemDoc
  def getOrCreateProductCollectionItemDoc = MessageDoc(
    process = "obp.getOrCreateProductCollectionItem",
    messageFormat = messageFormat,
    description = "Get Or Create Product Collection Item",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetOrCreateProductCollectionItem(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      collectionCode="string",
      memberProductCodes=List("string"))
    ),
    exampleInboundMessage = (
     InBoundGetOrCreateProductCollectionItem(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( ProductCollectionItemCommons(collectionCode="string",
      memberProductCode="string")))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getOrCreateProductCollectionItem(collectionCode: String, memberProductCodes: List[String], callContext: Option[CallContext]): OBPReturnType[Box[List[ProductCollectionItem]]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetOrCreateProductCollectionItem => InBound, OutBoundGetOrCreateProductCollectionItem => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , collectionCode, memberProductCodes)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[ProductCollectionItemCommons]](callContext))


  }
          
  messageDocs += getProductCollectionItemDoc
  def getProductCollectionItemDoc = MessageDoc(
    process = "obp.getProductCollectionItem",
    messageFormat = messageFormat,
    description = "Get Product Collection Item",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetProductCollectionItem(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      collectionCode="string")
    ),
    exampleInboundMessage = (
     InBoundGetProductCollectionItem(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( ProductCollectionItemCommons(collectionCode="string",
      memberProductCode="string")))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getProductCollectionItem(collectionCode: String, callContext: Option[CallContext]): OBPReturnType[Box[List[ProductCollectionItem]]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetProductCollectionItem => InBound, OutBoundGetProductCollectionItem => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , collectionCode)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[ProductCollectionItemCommons]](callContext))


  }
          
  messageDocs += getProductCollectionItemsTreeDoc
  def getProductCollectionItemsTreeDoc = MessageDoc(
    process = "obp.getProductCollectionItemsTree",
    messageFormat = messageFormat,
    description = "Get Product Collection Items Tree",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetProductCollectionItemsTree(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      collectionCode="string",
      bankId=bankIdExample.value)
    ),
    exampleInboundMessage = (
     InBoundGetProductCollectionItemsTree(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List(( ProductCollectionItemCommons(collectionCode="string",
      memberProductCode="string"),  ProductCommons(bankId=BankId(bankIdExample.value),
      code=ProductCode("string"),
      parentProductCode=ProductCode("string"),
      name="string",
      category="string",
      family="string",
      superFamily="string",
      moreInfoUrl="string",
      details="string",
      description="string",
      meta=Meta( License(id="string",
      name="string"))), List( ProductAttributeCommons(bankId=BankId(bankIdExample.value),
      productCode=ProductCode("string"),
      productAttributeId="string",
      name="string",
      attributeType=com.openbankproject.commons.model.enums.ProductAttributeType.example,
      value=valueExample.value)))))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getProductCollectionItemsTree(collectionCode: String, bankId: String, callContext: Option[CallContext]): OBPReturnType[Box[List[(ProductCollectionItem, Product, List[ProductAttribute])]]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetProductCollectionItemsTree => InBound, OutBoundGetProductCollectionItemsTree => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , collectionCode, bankId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[(ProductCollectionItemCommons, ProductCommons, List[ProductAttributeCommons])]](callContext))


  }
          
  messageDocs += createMeetingDoc
  def createMeetingDoc = MessageDoc(
    process = "obp.createMeeting",
    messageFormat = messageFormat,
    description = "Create Meeting",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundCreateMeeting(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      staffUser= UserCommons(userPrimaryKey=UserPrimaryKey(123),
      userId=userIdExample.value,
      idGivenByProvider="string",
      provider="string",
      emailAddress=emailExample.value,
      name=usernameExample.value),
      customerUser= UserCommons(userPrimaryKey=UserPrimaryKey(123),
      userId=userIdExample.value,
      idGivenByProvider="string",
      provider="string",
      emailAddress=emailExample.value,
      name=usernameExample.value),
      providerId="string",
      purposeId="string",
      when=new Date(),
      sessionId=sessionIdExample.value,
      customerToken="string",
      staffToken="string",
      creator= ContactDetails(name="string",
      phone="string",
      email=emailExample.value),
      invitees=List( Invitee(contactDetails= ContactDetails(name="string",
      phone="string",
      email=emailExample.value),
      status="string")))
    ),
    exampleInboundMessage = (
     InBoundCreateMeeting(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= MeetingCommons(meetingId="string",
      providerId="string",
      purposeId="string",
      bankId=bankIdExample.value,
      present= MeetingPresent(staffUserId="string",
      customerUserId="string"),
      keys= MeetingKeys(sessionId=sessionIdExample.value,
      customerToken="string",
      staffToken="string"),
      when=new Date(),
      creator= ContactDetails(name="string",
      phone="string",
      email=emailExample.value),
      invitees=List( Invitee(contactDetails= ContactDetails(name="string",
      phone="string",
      email=emailExample.value),
      status="string"))))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def createMeeting(bankId: BankId, staffUser: User, customerUser: User, providerId: String, purposeId: String, when: Date, sessionId: String, customerToken: String, staffToken: String, creator: ContactDetails, invitees: List[Invitee], callContext: Option[CallContext]): OBPReturnType[Box[Meeting]] = {
    

        import com.openbankproject.commons.dto.{InBoundCreateMeeting => InBound, OutBoundCreateMeeting => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, staffUser, customerUser, providerId, purposeId, when, sessionId, customerToken, staffToken, creator, invitees)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[MeetingCommons](callContext))


  }
          
  messageDocs += getMeetingsDoc
  def getMeetingsDoc = MessageDoc(
    process = "obp.getMeetings",
    messageFormat = messageFormat,
    description = "Get Meetings",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetMeetings(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      user= UserCommons(userPrimaryKey=UserPrimaryKey(123),
      userId=userIdExample.value,
      idGivenByProvider="string",
      provider="string",
      emailAddress=emailExample.value,
      name=usernameExample.value))
    ),
    exampleInboundMessage = (
     InBoundGetMeetings(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( MeetingCommons(meetingId="string",
      providerId="string",
      purposeId="string",
      bankId=bankIdExample.value,
      present= MeetingPresent(staffUserId="string",
      customerUserId="string"),
      keys= MeetingKeys(sessionId=sessionIdExample.value,
      customerToken="string",
      staffToken="string"),
      when=new Date(),
      creator= ContactDetails(name="string",
      phone="string",
      email=emailExample.value),
      invitees=List( Invitee(contactDetails= ContactDetails(name="string",
      phone="string",
      email=emailExample.value),
      status="string")))))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getMeetings(bankId: BankId, user: User, callContext: Option[CallContext]): OBPReturnType[Box[List[Meeting]]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetMeetings => InBound, OutBoundGetMeetings => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, user)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[MeetingCommons]](callContext))


  }
          
  messageDocs += getMeetingDoc
  def getMeetingDoc = MessageDoc(
    process = "obp.getMeeting",
    messageFormat = messageFormat,
    description = "Get Meeting",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetMeeting(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=BankId(bankIdExample.value),
      user= UserCommons(userPrimaryKey=UserPrimaryKey(123),
      userId=userIdExample.value,
      idGivenByProvider="string",
      provider="string",
      emailAddress=emailExample.value,
      name=usernameExample.value),
      meetingId="string")
    ),
    exampleInboundMessage = (
     InBoundGetMeeting(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= MeetingCommons(meetingId="string",
      providerId="string",
      purposeId="string",
      bankId=bankIdExample.value,
      present= MeetingPresent(staffUserId="string",
      customerUserId="string"),
      keys= MeetingKeys(sessionId=sessionIdExample.value,
      customerToken="string",
      staffToken="string"),
      when=new Date(),
      creator= ContactDetails(name="string",
      phone="string",
      email=emailExample.value),
      invitees=List( Invitee(contactDetails= ContactDetails(name="string",
      phone="string",
      email=emailExample.value),
      status="string"))))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getMeeting(bankId: BankId, user: User, meetingId: String, callContext: Option[CallContext]): OBPReturnType[Box[Meeting]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetMeeting => InBound, OutBoundGetMeeting => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, user, meetingId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[MeetingCommons](callContext))


  }
          
  messageDocs += createOrUpdateKycCheckDoc
  def createOrUpdateKycCheckDoc = MessageDoc(
    process = "obp.createOrUpdateKycCheck",
    messageFormat = messageFormat,
    description = "Create Or Update Kyc Check",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundCreateOrUpdateKycCheck(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=bankIdExample.value,
      customerId=customerIdExample.value,
      id="string",
      customerNumber=customerNumberExample.value,
      date=new Date(),
      how="string",
      staffUserId="string",
      mStaffName="string",
      mSatisfied=true,
      comments="string")
    ),
    exampleInboundMessage = (
     InBoundCreateOrUpdateKycCheck(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= KycCheckCommons(bankId=bankIdExample.value,
      customerId=customerIdExample.value,
      idKycCheck="string",
      customerNumber=customerNumberExample.value,
      date=new Date(),
      how="string",
      staffUserId="string",
      staffName="string",
      satisfied=true,
      comments="string"))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def createOrUpdateKycCheck(bankId: String, customerId: String, id: String, customerNumber: String, date: Date, how: String, staffUserId: String, mStaffName: String, mSatisfied: Boolean, comments: String, callContext: Option[CallContext]): OBPReturnType[Box[KycCheck]] = {
    

        import com.openbankproject.commons.dto.{InBoundCreateOrUpdateKycCheck => InBound, OutBoundCreateOrUpdateKycCheck => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, customerId, id, customerNumber, date, how, staffUserId, mStaffName, mSatisfied, comments)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[KycCheckCommons](callContext))


  }
          
  messageDocs += createOrUpdateKycDocumentDoc
  def createOrUpdateKycDocumentDoc = MessageDoc(
    process = "obp.createOrUpdateKycDocument",
    messageFormat = messageFormat,
    description = "Create Or Update Kyc Document",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundCreateOrUpdateKycDocument(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=bankIdExample.value,
      customerId=customerIdExample.value,
      id="string",
      customerNumber=customerNumberExample.value,
      `type`="string",
      number="string",
      issueDate=new Date(),
      issuePlace="string",
      expiryDate=new Date())
    ),
    exampleInboundMessage = (
     InBoundCreateOrUpdateKycDocument(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= KycDocumentCommons(bankId=bankIdExample.value,
      customerId=customerIdExample.value,
      idKycDocument="string",
      customerNumber=customerNumberExample.value,
      `type`="string",
      number="string",
      issueDate=new Date(),
      issuePlace="string",
      expiryDate=new Date()))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def createOrUpdateKycDocument(bankId: String, customerId: String, id: String, customerNumber: String, `type`: String, number: String, issueDate: Date, issuePlace: String, expiryDate: Date, callContext: Option[CallContext]): OBPReturnType[Box[KycDocument]] = {
    

        import com.openbankproject.commons.dto.{InBoundCreateOrUpdateKycDocument => InBound, OutBoundCreateOrUpdateKycDocument => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, customerId, id, customerNumber, `type`, number, issueDate, issuePlace, expiryDate)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[KycDocument](callContext))


  }
          
  messageDocs += createOrUpdateKycMediaDoc
  def createOrUpdateKycMediaDoc = MessageDoc(
    process = "obp.createOrUpdateKycMedia",
    messageFormat = messageFormat,
    description = "Create Or Update Kyc Media",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundCreateOrUpdateKycMedia(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=bankIdExample.value,
      customerId=customerIdExample.value,
      id="string",
      customerNumber=customerNumberExample.value,
      `type`="string",
      url=urlExample.value,
      date=new Date(),
      relatesToKycDocumentId="string",
      relatesToKycCheckId="string")
    ),
    exampleInboundMessage = (
     InBoundCreateOrUpdateKycMedia(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= KycMediaCommons(bankId=bankIdExample.value,
      customerId=customerIdExample.value,
      idKycMedia="string",
      customerNumber=customerNumberExample.value,
      `type`="string",
      url=urlExample.value,
      date=new Date(),
      relatesToKycDocumentId="string",
      relatesToKycCheckId="string"))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def createOrUpdateKycMedia(bankId: String, customerId: String, id: String, customerNumber: String, `type`: String, url: String, date: Date, relatesToKycDocumentId: String, relatesToKycCheckId: String, callContext: Option[CallContext]): OBPReturnType[Box[KycMedia]] = {
    

        import com.openbankproject.commons.dto.{InBoundCreateOrUpdateKycMedia => InBound, OutBoundCreateOrUpdateKycMedia => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, customerId, id, customerNumber, `type`, url, date, relatesToKycDocumentId, relatesToKycCheckId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[KycMediaCommons](callContext))


  }
          
  messageDocs += createOrUpdateKycStatusDoc
  def createOrUpdateKycStatusDoc = MessageDoc(
    process = "obp.createOrUpdateKycStatus",
    messageFormat = messageFormat,
    description = "Create Or Update Kyc Status",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundCreateOrUpdateKycStatus(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      bankId=bankIdExample.value,
      customerId=customerIdExample.value,
      customerNumber=customerNumberExample.value,
      ok=true,
      date=new Date())
    ),
    exampleInboundMessage = (
     InBoundCreateOrUpdateKycStatus(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= KycStatusCommons(bankId=bankIdExample.value,
      customerId=customerIdExample.value,
      customerNumber=customerNumberExample.value,
      ok=true,
      date=new Date()))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def createOrUpdateKycStatus(bankId: String, customerId: String, customerNumber: String, ok: Boolean, date: Date, callContext: Option[CallContext]): OBPReturnType[Box[KycStatus]] = {
    

        import com.openbankproject.commons.dto.{InBoundCreateOrUpdateKycStatus => InBound, OutBoundCreateOrUpdateKycStatus => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , bankId, customerId, customerNumber, ok, date)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[KycStatusCommons](callContext))


  }
          
  messageDocs += getKycChecksDoc
  def getKycChecksDoc = MessageDoc(
    process = "obp.getKycChecks",
    messageFormat = messageFormat,
    description = "Get Kyc Checks",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetKycChecks(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      customerId=customerIdExample.value)
    ),
    exampleInboundMessage = (
     InBoundGetKycChecks(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( KycCheckCommons(bankId=bankIdExample.value,
      customerId=customerIdExample.value,
      idKycCheck="string",
      customerNumber=customerNumberExample.value,
      date=new Date(),
      how="string",
      staffUserId="string",
      staffName="string",
      satisfied=true,
      comments="string")))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getKycChecks(customerId: String, callContext: Option[CallContext]): OBPReturnType[Box[List[KycCheck]]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetKycChecks => InBound, OutBoundGetKycChecks => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , customerId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[KycCheckCommons]](callContext))


  }
          
  messageDocs += getKycDocumentsDoc
  def getKycDocumentsDoc = MessageDoc(
    process = "obp.getKycDocuments",
    messageFormat = messageFormat,
    description = "Get Kyc Documents",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetKycDocuments(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      customerId=customerIdExample.value)
    ),
    exampleInboundMessage = (
     InBoundGetKycDocuments(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( KycDocumentCommons(bankId=bankIdExample.value,
      customerId=customerIdExample.value,
      idKycDocument="string",
      customerNumber=customerNumberExample.value,
      `type`="string",
      number="string",
      issueDate=new Date(),
      issuePlace="string",
      expiryDate=new Date())))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getKycDocuments(customerId: String, callContext: Option[CallContext]): OBPReturnType[Box[List[KycDocument]]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetKycDocuments => InBound, OutBoundGetKycDocuments => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , customerId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[KycDocumentCommons]](callContext))


  }
          
  messageDocs += getKycMediasDoc
  def getKycMediasDoc = MessageDoc(
    process = "obp.getKycMedias",
    messageFormat = messageFormat,
    description = "Get Kyc Medias",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetKycMedias(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      customerId=customerIdExample.value)
    ),
    exampleInboundMessage = (
     InBoundGetKycMedias(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( KycMediaCommons(bankId=bankIdExample.value,
      customerId=customerIdExample.value,
      idKycMedia="string",
      customerNumber=customerNumberExample.value,
      `type`="string",
      url=urlExample.value,
      date=new Date(),
      relatesToKycDocumentId="string",
      relatesToKycCheckId="string")))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getKycMedias(customerId: String, callContext: Option[CallContext]): OBPReturnType[Box[List[KycMedia]]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetKycMedias => InBound, OutBoundGetKycMedias => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , customerId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[KycMediaCommons]](callContext))


  }
          
  messageDocs += getKycStatusesDoc
  def getKycStatusesDoc = MessageDoc(
    process = "obp.getKycStatuses",
    messageFormat = messageFormat,
    description = "Get Kyc Statuses",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundGetKycStatuses(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      customerId=customerIdExample.value)
    ),
    exampleInboundMessage = (
     InBoundGetKycStatuses(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=List( KycStatusCommons(bankId=bankIdExample.value,
      customerId=customerIdExample.value,
      customerNumber=customerNumberExample.value,
      ok=true,
      date=new Date())))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def getKycStatuses(customerId: String, callContext: Option[CallContext]): OBPReturnType[Box[List[KycStatus]]] = {
    

        import com.openbankproject.commons.dto.{InBoundGetKycStatuses => InBound, OutBoundGetKycStatuses => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , customerId)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[List[KycStatusCommons]](callContext))


  }
          
  messageDocs += createMessageDoc
  def createMessageDoc = MessageDoc(
    process = "obp.createMessage",
    messageFormat = messageFormat,
    description = "Create Message",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundCreateMessage(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      user= UserCommons(userPrimaryKey=UserPrimaryKey(123),
      userId=userIdExample.value,
      idGivenByProvider="string",
      provider="string",
      emailAddress=emailExample.value,
      name=usernameExample.value),
      bankId=BankId(bankIdExample.value),
      message="string",
      fromDepartment="string",
      fromPerson="string")
    ),
    exampleInboundMessage = (
     InBoundCreateMessage(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data= CustomerMessageCommons(messageId="string",
      date=new Date(),
      message="string",
      fromDepartment="string",
      fromPerson="string"))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def createMessage(user: User, bankId: BankId, message: String, fromDepartment: String, fromPerson: String, callContext: Option[CallContext]): OBPReturnType[Box[CustomerMessage]] = {
    

        import com.openbankproject.commons.dto.{InBoundCreateMessage => InBound, OutBoundCreateMessage => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , user, bankId, message, fromDepartment, fromPerson)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[CustomerMessageCommons](callContext))


  }
          
  messageDocs += makeHistoricalPaymentDoc
  def makeHistoricalPaymentDoc = MessageDoc(
    process = "obp.makeHistoricalPayment",
    messageFormat = messageFormat,
    description = "Make Historical Payment",
    outboundTopic = None,
    inboundTopic = None,
    exampleOutboundMessage = (
     OutBoundMakeHistoricalPayment(outboundAdapterCallContext=MessageDocsSwaggerDefinitions.outboundAdapterCallContext,
      fromAccount= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=bankAccountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value),
      toAccount= BankAccountCommons(accountId=AccountId(accountIdExample.value),
      accountType=accountTypeExample.value,
      balance=BigDecimal(balanceAmountExample.value),
      currency=currencyExample.value,
      name=bankAccountNameExample.value,
      label=labelExample.value,
      iban=Some(ibanExample.value),
      number=bankAccountNumberExample.value,
      bankId=BankId(bankIdExample.value),
      lastUpdate=parseDate(bankAccountLastUpdateExample.value).getOrElse(sys.error("bankAccountLastUpdateExample.value is not validate date format.")),
      branchId=branchIdExample.value,
      accountRoutingScheme=accountRoutingSchemeExample.value,
      accountRoutingAddress=accountRoutingAddressExample.value,
      accountRoutings=List( AccountRouting(scheme=accountRoutingSchemeExample.value,
      address=accountRoutingAddressExample.value)),
      accountRules=List( AccountRule(scheme=accountRuleSchemeExample.value,
      value=accountRuleValueExample.value)),
      accountHolder=bankAccountAccountHolderExample.value),
      posted=new Date(),
      completed=new Date(),
      amount=BigDecimal("123.321"),
      description="string",
      transactionRequestType=transactionRequestTypeExample.value,
      chargePolicy="string")
    ),
    exampleInboundMessage = (
     InBoundMakeHistoricalPayment(inboundAdapterCallContext=MessageDocsSwaggerDefinitions.inboundAdapterCallContext,
      status=MessageDocsSwaggerDefinitions.inboundStatus,
      data=TransactionId(transactionIdExample.value))
    ),
    adapterImplementation = Some(AdapterImplementation("- Core", 1))
  )

  override def makeHistoricalPayment(fromAccount: BankAccount, toAccount: BankAccount, posted: Date, completed: Date, amount: BigDecimal, description: String, transactionRequestType: String, chargePolicy: String, callContext: Option[CallContext]): OBPReturnType[Box[TransactionId]] = {
    

        import com.openbankproject.commons.dto.{InBoundMakeHistoricalPayment => InBound, OutBoundMakeHistoricalPayment => OutBound}
        val req = OutBound(callContext.map(_.toOutboundAdapterCallContext).orNull , fromAccount, toAccount, posted, completed, amount, description, transactionRequestType, chargePolicy)
        val response: Future[Box[InBound]] = (southSideActor ? req).mapTo[InBound].map(Box !!(_))
        response.map(convertToTuple[TransactionId](callContext))


  }
          
// ---------- create on Mon Jun 15 12:48:47 CST 2020
//---------------- dynamic end ---------------------please don't modify this line         
}
