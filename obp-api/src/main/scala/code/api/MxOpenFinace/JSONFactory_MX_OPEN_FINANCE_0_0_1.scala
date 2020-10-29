package code.api.MxOpenFinace

import java.util.Date

import code.api.Constant
import code.api.util.CustomJsonFormats
import code.model.{ModeratedBankAccountCore, ModeratedTransaction}
import com.openbankproject.commons.model._
import net.liftweb.json.JValue

import scala.collection.immutable.List

case class JvalueCaseClass(jvalueToCaseclass: JValue)

object JSONFactory_MX_OPEN_FINANCE_0_0_1 extends CustomJsonFormats {

  case class MetaMXOFV001(LastAvailableDateTime: Date, FirstAvailableDateTime: Date, TotalPages: Int)
  case class LinksMXOFV001(Self: String, First: Option[String], Prev: Option[String], Next: Option[String], Last: Option[String] )
  case class ReadAccountBasicMXOFV001(Data: DataAccountBasicMXOFV001, Links: LinksMXOFV001, Meta: MetaMXOFV001)
  case class DataAccountBasicMXOFV001(Account: List[AccountBasicMXOFV001])
  case class ServicerMXOFV001(SchemeName: String,
                              Identification: String)
  case class AccountBasicMXOFV001(AccountId: String,
                                  Status: String,
                                  StatusUpdateDateTime: String,
                                  Currency: String,
                                  AccountType: String,
                                  AccountSubType: String,
                                  AccountIndicator: String,
                                  OnboardingType: Option[String],
                                  Nickname: Option[String],
                                  OpeningDate: Option[String],
                                  MaturityDate: Option[String],
                                  Account: Option[AccountDetailMXOFV001],
                                  Servicer: Option[ServicerMXOFV001]
                               )
  case class AccountDetailMXOFV001(
                                  SchemeName: String,
                                  Identification: String,
                                  Name: Option[String]
                                )

  case class AmountMXOFV001(
    Amount: String,
    Currency: String
  )
  case class CurrencyExchangeMXOFV001(
    SourceCurrency: String,
    TargetCurrency: String,
    UnitCurrency: Option[String],
    ExchangeRate: String,
    ContractIdentification: Option[String],
    QuotationDate: Option[String],
    InstructedAmount: Option[AmountMXOFV001]
  )
  case class BankTransactionCodeMXOFV001(
    Code: String,
    SubCode: String
  )
  case class CardInstrumentMXOFV001(
    CardSchemeName: String,
    AuthorisationType: String,
    Name: String,
    Identification: String
  )
  case class AdditionalProp1MXOFV001(

  )
  case class SupplementaryDataMXOFV001(
    additionalProp1: AdditionalProp1MXOFV001
  )

  case class BalanceMXOFV001(
                              AccountIndicator: String,
                              Type: String = "ClosingAvailable",
                              Amount: AmountMXOFV001
                            )

  case class MerchantDetailsMXOFV001(
                                      MerchantName: Option[String],
                                      MerchantCategoryCode: Option[String]
                                    )

  case class TransactionRecipientMXOFV001(
                                           SchemeName: Option[String],
                                           Identification: Option[String],
                                           Name: String
                                         )

  case class RecipientAccountMXOFV001(
                                       SchemeName: Option[String],
                                       Identification: String,
                                       Name: Option[String]
                                     )

  case class TransactionSenderMXOFV001(
                                        SchemeName: Option[String],
                                        Identification: Option[String],
                                        Name: String
                                      )

  case class SenderAccountMXOFV001(
                                    SchemeName: Option[String],
                                    Identification: String,
                                    Name: Option[String]
                                  )
  
  case class TransactionBasicMXOFV001(
                                       AccountId: String,
                                       TransactionId: String,
                                       TransactionReference: Option[String],
                                       TransferTracingCode: Option[String],
                                       AccountIndicator: String,
                                       Status: String,
                                       BookingDateTime: String,
                                       ValueDateTime: Option[String],
                                       TransactionInformation: String,
                                       AddressLine: Option[String],
                                       Amount: AmountMXOFV001,
                                       CurrencyExchange: Option[CurrencyExchangeMXOFV001],
                                       BankTransactionCode: Option[BankTransactionCodeMXOFV001],
                                       Balance: Option[BalanceMXOFV001],
                                       MerchantDetails: Option[MerchantDetailsMXOFV001],
                                       TransactionRecipient: Option[TransactionRecipientMXOFV001],
                                       RecipientAccount: Option[RecipientAccountMXOFV001],
                                       TransactionSender: Option[TransactionSenderMXOFV001],
                                       SenderAccount: Option[SenderAccountMXOFV001],
                                       CardInstrument: Option[CardInstrumentMXOFV001],
                                       SupplementaryData: Option[SupplementaryDataMXOFV001]
  )

  case class TransactionMXOFV001(
    Transaction:List[TransactionBasicMXOFV001]
  )
  
  case class ReadTransactionMXOFV001(Data: TransactionMXOFV001, Links: LinksMXOFV001, Meta: MetaMXOFV001)

  case class ConsentPostBodyDataMXOFV001(
    TransactionToDateTime: String,
    ExpirationDateTime: String,
    Permissions: List[String],
    TransactionFromDateTime: String
  )
  case class ConsentPostBodyMXOFV001(
    Data: ConsentPostBodyDataMXOFV001
  )
  
  
  lazy val metaMocked = MetaMXOFV001(
    LastAvailableDateTime = new Date(),
    FirstAvailableDateTime = new Date(),
    TotalPages = 0
  )
  lazy val linksMocked = LinksMXOFV001(Self = "Self", None, None, None, None)

  lazy val accountBasic = AccountBasicMXOFV001(
    AccountId = "string",
    Status = "Enabled",
    StatusUpdateDateTime = "2020-08-28T06:44:05.618Z",
    Currency = "string",
    AccountType = "RegularAccount",
    AccountSubType = "Personal",
    AccountIndicator = "Debit",
    OnboardingType = Some("OnSite"),
    Nickname = Some("Amazon"),
    OpeningDate = Some("2020-08-28T06:44:05.618Z"),
    MaturityDate = Some("2020-08-28T06:44:05.618Z"),
    Account = Some(
        AccountDetailMXOFV001(
        SchemeName = "string",
        Identification = "string",
        Name = Some("string")
      )
    ),
    Servicer = Some(ServicerMXOFV001(SchemeName = "string", Identification = "string"))
  )

  lazy val ofReadAccountBasic = ReadAccountBasicMXOFV001(Meta = metaMocked, Links = linksMocked, Data = DataAccountBasicMXOFV001(Account = List(accountBasic)))

  val dataJson = DataJsonMXOFV001(
    List(BalanceJsonMXOFV001(
    AccountId = "accountId",
    Amount = AmountOfMoneyJsonV121("currency", "1000"),
    CreditDebitIndicator = "Credit",
    Type = "Available",
    DateTime = new Date(),
    CreditLine = List(CreditLineJsonMXOFV001(
      Included = true,
      Amount = AmountOfMoneyJsonV121("currency", "1000"),
      Type = "Pre-Agreed"
    )))))
  
  lazy val ofReadBalances = AccountBalancesMXOFV001(
    Data = dataJson,
    Links = LinksMXOFV001(
      s"${Constant.HostName}/mx-open-finance/v0.0.1/accounts/accountId/balances",
      None,
      None,
      None,
      None),
    Meta = MetaMXOFV001(
      new Date(),
      new Date(),
      0
    )
  )

  private def accountAttributeOptValue(name: String, 
                                            bankId: BankId, 
                                            accountId: AccountId, 
                                            list: List[AccountAttribute]): Option[String] =
    list.filter(e => e.name == name && e.bankId == bankId && e.accountId == accountId).headOption.map(_.value)
  private def accountAttributeValue(name: String, 
                                    bankId: BankId,
                                    accountId: AccountId, 
                                    list: List[AccountAttribute]): String =
    accountAttributeOptValue(name, bankId, accountId, list).getOrElse(null)

  private def transactionAttributeOptValue(name: String,
                                           bankId: BankId,
                                           transactionId: TransactionId,
                                           list: List[TransactionAttribute]): Option[String] =
    list.filter(e => e.name == name && e.bankId == bankId && e.transactionId == transactionId).headOption.map(_.value)

  private def transactionAttributeValue(name: String,
                                        bankId: BankId,
                                        transactionId: TransactionId,
                                        list: List[TransactionAttribute]): String =
    transactionAttributeOptValue(name, bankId, transactionId, list).getOrElse(null)
  
  def createReadAccountBasicJsonMXOFV10(account : ModeratedBankAccountCore, 
                                        moderatedAttributes: List[AccountAttribute],
                                        view: View): ReadAccountBasicMXOFV001 = {

    def getServicer: Option[ServicerMXOFV001] = {
      view.viewId.value match {
        case Constant.READ_ACCOUNTS_DETAIL_VIEW_ID =>
          val schemeName = accountAttributeValue("Servicer_SchemeName", account.bankId, account.accountId, moderatedAttributes)
          val identification = accountAttributeValue("Servicer_Identification", account.bankId, account.accountId, moderatedAttributes)
          val result = ServicerMXOFV001(
            SchemeName = schemeName,
            Identification = identification
          )
          if (schemeName != null || identification != null) Some(result) else None
        case _ =>
          None
      }
    }

    def getAccountDetails: Option[AccountDetailMXOFV001] = {
      view.viewId.value match {
        case Constant.READ_ACCOUNTS_DETAIL_VIEW_ID =>
          account.accountRoutings.headOption.map(e =>
            AccountDetailMXOFV001(SchemeName = e.scheme, Identification = e.address, None)
          )
        case _ =>
          None
      }
    }

    val accountBasic = AccountBasicMXOFV001(
      AccountId = account.accountId.value,
      Status = accountAttributeValue("Status", account.bankId, account.accountId, moderatedAttributes),
      StatusUpdateDateTime = accountAttributeValue("StatusUpdateDateTime", account.bankId, account.accountId, moderatedAttributes),
      Currency = account.currency.getOrElse(""),
      AccountType = account.accountType.getOrElse(""),
      AccountSubType = accountAttributeValue("AccountSubType", account.bankId, account.accountId, moderatedAttributes),
      AccountIndicator = accountAttributeValue("AccountIndicator", account.bankId, account.accountId, moderatedAttributes),
      OnboardingType = accountAttributeOptValue("OnboardingType", account.bankId, account.accountId, moderatedAttributes),
      Nickname = account.label,
      OpeningDate = accountAttributeOptValue("OpeningDate", account.bankId, account.accountId, moderatedAttributes),
      MaturityDate = accountAttributeOptValue("MaturityDate", account.bankId, account.accountId, moderatedAttributes),
      Account = getAccountDetails,
      Servicer = getServicer
    )
    val links = LinksMXOFV001(
      s"${Constant.HostName}/mx-open-finance/v0.0.1/accounts/" + account.accountId.value,
      None,
      None,
      None,
      None
    )
    val meta = MetaMXOFV001(
      TotalPages = 1,
      FirstAvailableDateTime = new Date(),
      LastAvailableDateTime = new Date()
    )
    ReadAccountBasicMXOFV001(Meta = meta, Links = links, Data = DataAccountBasicMXOFV001(Account = List(accountBasic)))
  }
  def createReadAccountsBasicJsonMXOFV10(accounts : List[(BankAccount, View)],
                                         moderatedAttributes: List[AccountAttribute]): ReadAccountBasicMXOFV001 = {
    def getServicer(account: (BankAccount, View)): Option[ServicerMXOFV001] = {
      account._2.viewId.value match {
        case Constant.READ_ACCOUNTS_DETAIL_VIEW_ID =>
          val schemeName = accountAttributeValue("Servicer_SchemeName", account._1.bankId, account._1.accountId, moderatedAttributes)
          val identification = accountAttributeValue("Servicer_Identification", account._1.bankId, account._1.accountId, moderatedAttributes)
          val result = ServicerMXOFV001(
              SchemeName = schemeName,
              Identification = identification
          )
          if (schemeName != null || identification != null) Some(result) else None
        case _ =>
          None
      }
    }

    def getAccountDetails(account: (BankAccount, View)): Option[AccountDetailMXOFV001] = {
      account._2.viewId.value match {
        case Constant.READ_ACCOUNTS_DETAIL_VIEW_ID =>
          account._1.accountRoutings.headOption.map(e =>
            AccountDetailMXOFV001(
              SchemeName = e.scheme, 
              Identification = e.address, 
              Name = None)
          )
        case _ =>
          None
      }
    }

    val accountsBasic = accounts.map(account =>
      AccountBasicMXOFV001(
        AccountId = account._1.accountId.value,
        Status = accountAttributeValue("Status", account._1.bankId, account._1.accountId, moderatedAttributes),
        StatusUpdateDateTime = accountAttributeValue("StatusUpdateDateTime", account._1.bankId, account._1.accountId, moderatedAttributes),
        Currency = account._1.currency,
        AccountType = account._1.accountType,
        AccountSubType = accountAttributeValue("AccountSubType", account._1.bankId, account._1.accountId, moderatedAttributes),
        AccountIndicator = accountAttributeValue("AccountIndicator", account._1.bankId, account._1.accountId, moderatedAttributes),
        OnboardingType = accountAttributeOptValue("OnboardingType", account._1.bankId, account._1.accountId, moderatedAttributes),
        Nickname = Some(account._1.label),
        OpeningDate = accountAttributeOptValue("OpeningDate", account._1.bankId, account._1.accountId, moderatedAttributes),
        MaturityDate = accountAttributeOptValue("MaturityDate", account._1.bankId, account._1.accountId, moderatedAttributes),
        Account = getAccountDetails(account),
        Servicer = getServicer(account)
      )
    )
    val links = LinksMXOFV001(
      s"${Constant.HostName}/mx-open-finance/v0.0.1/accounts/",
      None,
      None,
      None,
      None
    )
    val meta = MetaMXOFV001(
      TotalPages = 1,
      FirstAvailableDateTime = new Date(),
      LastAvailableDateTime = new Date()
    )
    ReadAccountBasicMXOFV001(Meta = meta, Links = links, Data = DataAccountBasicMXOFV001(Account = accountsBasic))
  }

  def createGetTransactionsByAccountIdMXOFV10(bankId: BankId,
                                              moderatedTransactions : List[ModeratedTransaction],
                                              attributes: List[TransactionAttribute],
                                              view: View): ReadTransactionMXOFV001 = {

    val accountId = moderatedTransactions.map(_.bankAccount.map(_.accountId.value)).flatten.headOption.getOrElse("")

    def getMerchantDetails(moderatedTransaction: ModeratedTransaction) = {
      view.viewId.value match {
        case Constant.READ_TRANSACTIONS_DETAIL_VIEW_ID =>
          val merchantName = transactionAttributeOptValue("MerchantDetails_MerchantName", bankId, moderatedTransaction.id, attributes)
          val merchantCategoryCode = transactionAttributeOptValue("MerchantDetails_CategoryCode", bankId, moderatedTransaction.id, attributes)
          val result = MerchantDetailsMXOFV001(
              MerchantName = merchantName,
              MerchantCategoryCode = merchantCategoryCode
            )
          if (merchantName.isDefined || merchantCategoryCode.isDefined) Some(result) else None
        case _ => None
      }
    }

    def getBalance(moderatedTransaction: ModeratedTransaction) = {
      view.viewId.value match {
        case Constant.READ_TRANSACTIONS_DETAIL_VIEW_ID =>
          val accountIndicator = transactionAttributeValue("Balance_AccountIndicator", bankId, moderatedTransaction.id, attributes)
          val `type` = transactionAttributeValue("Balance_Type", bankId, moderatedTransaction.id, attributes)
          val amount = transactionAttributeValue("Balance_Amount", bankId, moderatedTransaction.id, attributes)
          val currency = transactionAttributeValue("Balance_Currency", bankId, moderatedTransaction.id, attributes)
          val result = BalanceMXOFV001(
              AccountIndicator = accountIndicator,
              Type = `type`,
              Amount = AmountMXOFV001(
                Amount = amount,
                Currency = currency,
              )
            )
          if (accountIndicator != null || `type` != null || amount != null || currency != null) Some(result) else None
        case _ => None
      }
    }

    def getTransactionRecipient(moderatedTransaction: ModeratedTransaction) = {
      view.viewId.value match {
        case Constant.READ_TRANSACTIONS_DETAIL_VIEW_ID =>
          val schemeName = transactionAttributeOptValue("Recipient_SchemeName", bankId, moderatedTransaction.id, attributes)
          val identification = transactionAttributeOptValue("Recipient_Identification", bankId, moderatedTransaction.id, attributes)
          val name = transactionAttributeValue("Recipient_Name", bankId, moderatedTransaction.id, attributes)
          val result = TransactionRecipientMXOFV001(
            SchemeName = schemeName,
            Identification = identification,
            Name = name
            )
          if (schemeName.isDefined || identification.isDefined || name != null) Some(result) else None
        case _ => None
      }
    }

    def getRecipientAccount(moderatedTransaction: ModeratedTransaction) = {
      view.viewId.value match {
        case Constant.READ_TRANSACTIONS_DETAIL_VIEW_ID =>
          val schemeName = transactionAttributeOptValue("RecipientAccount_SchemeName", bankId, moderatedTransaction.id, attributes)
          val identification = transactionAttributeValue("RecipientAccount_Identification", bankId, moderatedTransaction.id, attributes)
          val name = transactionAttributeOptValue("RecipientAccount_Name", bankId, moderatedTransaction.id, attributes)
          val result =  RecipientAccountMXOFV001(
             SchemeName = schemeName,
             Identification = identification,
             Name = name
            )
          if (schemeName.isDefined || identification != null || name.isDefined) Some(result) else None
        case _ => None
      }
    }

    def getTransactionSender(moderatedTransaction: ModeratedTransaction) = {
      view.viewId.value match {
        case Constant.READ_TRANSACTIONS_DETAIL_VIEW_ID =>
          val schemeName = transactionAttributeOptValue("Sender_SchemeName", bankId, moderatedTransaction.id, attributes)
          val identification = transactionAttributeOptValue("Sender_Identification", bankId, moderatedTransaction.id, attributes)
          val name = transactionAttributeValue("Sender_Name", bankId, moderatedTransaction.id, attributes)
          val result = TransactionSenderMXOFV001(
            SchemeName = schemeName,
            Identification = identification,
            Name = name
          )
          if (schemeName.isDefined || identification.isDefined || name != null) Some(result) else None
        case _ => None
      }
    }

    def getSenderAccount(moderatedTransaction: ModeratedTransaction) = {
      view.viewId.value match {
        case Constant.READ_TRANSACTIONS_DETAIL_VIEW_ID =>
          val schemeName = transactionAttributeOptValue("SenderAccount_SchemeName", bankId, moderatedTransaction.id, attributes)
          val identification = transactionAttributeValue("SenderAccount_Identification", bankId, moderatedTransaction.id, attributes)
          val name = transactionAttributeOptValue("SenderAccount_Name", bankId, moderatedTransaction.id, attributes)
          val result = SenderAccountMXOFV001(
            SchemeName = schemeName,
            Identification = identification,
            Name = name
            )
          if (schemeName.isDefined || identification != null || name.isDefined) Some(result) else None
        case _ => None
      }
    }

    def getCardInstrument(moderatedTransaction: ModeratedTransaction): Option[CardInstrumentMXOFV001] = {
      val cardSchemeName = transactionAttributeValue("Card_SchemeName", bankId, moderatedTransaction.id, attributes)
      val identification = transactionAttributeValue("Card_Identification", bankId, moderatedTransaction.id, attributes)
      val authorisationType = transactionAttributeValue("Card_AuthorizationType", bankId, moderatedTransaction.id, attributes)
      val name = transactionAttributeValue("Card_Name", bankId, moderatedTransaction.id, attributes)
      val result = CardInstrumentMXOFV001(
          CardSchemeName = cardSchemeName,
          AuthorisationType = authorisationType,
          Name = name,
          Identification = identification,
        )
      if (cardSchemeName != null || identification != null || authorisationType != null || name != null) Some(result) else None
    }

    def getInstructedAmount(moderatedTransaction: ModeratedTransaction): Option[AmountMXOFV001] = {
      val amount = transactionAttributeValue("CurrencyExchange_InstructedAmount", bankId, moderatedTransaction.id, attributes)
      val currency = transactionAttributeValue("CurrencyExchange_InstructedAmount_Currency", bankId, moderatedTransaction.id, attributes)
      val result = AmountMXOFV001(
          Amount = amount,
          Currency = currency,
      )
      if (amount != null || currency != null) Some(result) else None
    }

    def getBankTransactionCode(moderatedTransaction: ModeratedTransaction) = {
      val code = transactionAttributeValue("Code", bankId, moderatedTransaction.id, attributes)
      val subCode = transactionAttributeValue("SubCode", bankId, moderatedTransaction.id, attributes)
      val result = BankTransactionCodeMXOFV001(
          Code = code,
          SubCode = subCode,
        )
      if (code != null || subCode != null) Some(result) else None
    }

    val transactions = moderatedTransactions.map(
      moderatedTransaction =>
        TransactionBasicMXOFV001(
        AccountId = accountId,
        TransactionId = moderatedTransaction.id.value,
        TransactionReference = transactionAttributeOptValue("TransactionReference", bankId, moderatedTransaction.id, attributes),
        TransferTracingCode = transactionAttributeOptValue("TransferTracingCode", bankId, moderatedTransaction.id, attributes),
        AccountIndicator = moderatedTransaction.bankAccount.map(_.accountType).flatten.getOrElse(null),
        Status = "BOOKED", // [ Booked, Pending, Cancelled ]
        BookingDateTime = moderatedTransaction.startDate.map(_.toString).getOrElse(null),
        ValueDateTime = transactionAttributeOptValue("ValueDateTime", bankId, moderatedTransaction.id, attributes),
        TransactionInformation = moderatedTransaction.description.getOrElse(null),
        AddressLine = transactionAttributeOptValue("AddressLine", bankId, moderatedTransaction.id, attributes),
        Amount = AmountMXOFV001(
          moderatedTransaction.amount.map(_.bigDecimal.toString).getOrElse(null),
          moderatedTransaction.currency.getOrElse(null),
        ),
        CurrencyExchange = Some(CurrencyExchangeMXOFV001(
          SourceCurrency = transactionAttributeValue("CurrencyExchange_SourceCurrency", bankId, moderatedTransaction.id, attributes),
          TargetCurrency = transactionAttributeValue("CurrencyExchange_TargetCurrency", bankId, moderatedTransaction.id, attributes),
          UnitCurrency = transactionAttributeOptValue("CurrencyExchange_UnitCurrency", bankId, moderatedTransaction.id, attributes),
          ExchangeRate = transactionAttributeValue("CurrencyExchange_ExchangeRate", bankId, moderatedTransaction.id, attributes),
          ContractIdentification = transactionAttributeOptValue("CurrencyExchange_ContractIdentification", bankId, moderatedTransaction.id, attributes),
          QuotationDate = transactionAttributeOptValue("CurrencyExchange_QuotationDate", bankId, moderatedTransaction.id, attributes),
          InstructedAmount = getInstructedAmount(moderatedTransaction)
        )),
        BankTransactionCode = getBankTransactionCode(moderatedTransaction),
        Balance = getBalance(moderatedTransaction), 
        MerchantDetails = getMerchantDetails(moderatedTransaction),
        TransactionRecipient = getTransactionRecipient(moderatedTransaction),
        RecipientAccount = getRecipientAccount(moderatedTransaction),
        TransactionSender = getTransactionSender(moderatedTransaction),
        SenderAccount = getSenderAccount(moderatedTransaction),
        CardInstrument = getCardInstrument(moderatedTransaction),
        SupplementaryData = None,
      )
    )
    
    val links = LinksMXOFV001(
      s"${Constant.HostName}/mx-open-finance/v0.0.1/accounts/" + accountId + "/transactions",
      None,
      None,
      None,
      None
    )
    
    val meta = MetaMXOFV001(
      TotalPages = 1,
      FirstAvailableDateTime = new Date(),
      LastAvailableDateTime = new Date()
    )
    ReadTransactionMXOFV001(TransactionMXOFV001(transactions), Meta = meta, Links = links)
  }


  def createAccountBalanceJSON(moderatedAccount: ModeratedBankAccountCore) = {
    val accountId = moderatedAccount.accountId.value

    val dataJson = DataJsonMXOFV001(
      List(BalanceJsonMXOFV001(
        AccountId = accountId,
        Amount = AmountOfMoneyJsonV121(moderatedAccount.currency.getOrElse(""), moderatedAccount.balance.getOrElse("")),
        CreditDebitIndicator = "Credit",
        Type = "Available",
        DateTime = new Date(),
        CreditLine = List(CreditLineJsonMXOFV001(
          Included = true,
          Amount = AmountOfMoneyJsonV121(moderatedAccount.currency.getOrElse(""), moderatedAccount.balance.getOrElse("")),
          Type = "Pre-Agreed"
        )))))

    AccountBalancesMXOFV001(
      Data = dataJson,
      Links = LinksMXOFV001(
        s"${Constant.HostName}/mx-open-finance/v0.0.1/accounts/${accountId}/balances",
        None,
        None,
        None,
        None),
      Meta = MetaMXOFV001(
        new Date(),
        new Date(),
        0
      )
    )
  }

  case class CreditLineJsonMXOFV001(
                                   Included: Boolean,
                                   Amount: AmountOfMoneyJsonV121,
                                   Type: String
                                 )

  case class BalanceJsonMXOFV001(
                                AccountId: String,
                                Amount: AmountOfMoneyJsonV121,
                                CreditDebitIndicator: String,
                                Type: String,
                                DateTime: Date,
                                CreditLine: List[CreditLineJsonMXOFV001]
                              )

  case class DataJsonMXOFV001(
                             Balance: List[BalanceJsonMXOFV001]
                           )

  case class AccountBalancesMXOFV001(
                                      Data: DataJsonMXOFV001,
                                      Links: LinksMXOFV001,
                                      Meta: MetaMXOFV001
                                  )


}