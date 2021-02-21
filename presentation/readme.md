### MyBank Projesi Sunum

##### Veritabanı Tasarımı



![ER-Diagram](C:\Users\serda\Desktop\presentation\figures\ER-Diagram.png)

​																			**Şekil 1:** Projenin ER(Varlık İlişki) Diagramı

`Customer Tablosu: Customer tablosunda primary key customer_id, name,surname, phone_number(unique),tc(unique)  `

`Account Tablosu: Account tablosunda primary key account_id,account_type (CURRENT_ACCOUNT,SAVİNG_ACCOUNT), currency, iban(unique),money_type(TRY,USD,EUR), foreign key customer_id`

`Debitcard Tablosu: Debitcard tablosunda primary key card_number, ccv, expiration_month, expiration_year, password, foreing key account_id`

`Creditcard Tablosu: Creditcard tablosunda primary key card_number, ccv, credit, debt, expiration_month, expiration_year, password, foreign key customer_id.`

`Transaction Tablosu: Transaction tablosunda primary key id, explanation, performed_id, transaction_date, transaction_type(TRANSFER, WITHDRAWAL, DEPOSIT, PAY_DEBT, SHOPPING)`

##### Customer API

![CustomerDto](C:\Users\serda\Desktop\presentation\figures\CustomerDto.png)

​																					**Şekil 2:** CustomerDto Sınıfı



```
curl -i \ 
-H "Accept: application/json" \ 
-H "Content-Type:application/json" \ 
-X POST --data 
{CustomerDto}
POST http://localhost:8080/api/customer
```

create -> Sadece CustomerDto objesi alır.

```
POST http://localhost:8080/api/customer/{customerId}/updatePhone?phoneNumber=
```

updatePhone->  PathVariable olarak customerId , parametre olarak phoneNumber alır.

```
DELETE http://localhost:8080/api/customer?customerId=
```

delete->parametre olarak customerId alır.

```
GET http://localhost:8080/api/customer/{customerId}
```

get -> PathVariable olarak customerId alır.



##### Account Üzerinden Para Transfer İşlemi

![sendMoney](C:\Users\serda\Desktop\presentation\figures\sendMoney.png)

​															**Şekil 2:** Account ile para transfer fonksiyonu

- Gönderici accountIdsi ile göndericinin ibanı alınır.
- Alıcı ibanı ile kayıtlı accountu tanımlanır.
- Yeterli miktarda para var ise alıcı hesabının, para birimine göre exchagerateapi vasıtasıyla anlık kur değerleri alınır.
- Gönderme işlemi yapılır.
- Account tablosuna işlem yapılan hesaplar ve Transaction tablosuna işlemleri kayıt edilir.

##### HandleMethodArgumentNotValid Metodu

![image-20210221233517276](C:\Users\serda\Desktop\presentation\figures\handleMethodArgumentNotValid.png)

​														**Şekil 3:**  GlobalExceptionHandler Sınıfı içerisindeki handleMethodArgumentNotValid metodu

```
Çıktı:
{
  "timestamp": "2021-02-21T23:33:25.0371822",
  "status": 400,
  "errors": [
    "TC  length must be 11 and TC contains only alphanumeric characters.",
    "PhoneNumber Ex:+(123)-456-78-90"
  ]
}
```

