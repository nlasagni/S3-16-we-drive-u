
# WeDriveU Self-Driving Car Mobility Service 

### Descrizione.

L’azienda WeDriveU punta a diventare leader nei servizi di mobilità smart e per questo si è dotata di diverse self-driving car munite di connessione ad internet e gps e collocate all’interno di speciali punti-garage in punti strategici del paese e vuole realizzare un sistema in grado di gestire le prenotazioni e coordinare i veicoli.
Di seguito i servizi che l’azienda vuole offrire agli utenti e utilizzare nel proprio back-office.

### Servizi offerti agli utenti [SU]
 * Prenotazione di un veicolo tramite app per smartphone.  
 * Invio attraverso il sistema della macchina più vicina e con sufficiente autonomia di chilometri all’utente che ha effettuato la prenotazione.
 * Informazione all’utente di quanto un veicolo è distante e quanto impiegherà per arrivare al punto di partenza dell’utente.
 * Sostituzione del veicolo nel caso quello su cui è l’utente abbia un malfunzionamento

### Servizi offerti al back-office [SBO]
 * Eventuale gestione da remoto (anche solo visualizzazione di dati) da qualcuno dedicato al back office / amministrazione
 * Statistiche su veicoli in uso, veicoli fermi, veicoli non funzionanti, posizioni più frequenti di prenotazione, posizioni più frequenti di destinazione utili ad ottimizzare il servizio

### Utilizzo

Il sistema è composto da 3 parti client e 4 microservizi lato server.

I 4 microservizi sono già in esecuzione in un server remoto per tanto non è necessario eseguire i relativi file ".jar".

Come prima cosa è necessario avviare il jar "wedriveu-vehicle-xxx.jar" per creare delle simulazioni di veicoli che
saranno automaticamente aggiunti al sistema.

Il client principale è la App Android, contenuta nel file con estensione ".apk".
E' necessario scaricarla nel proprio dispositivo ed avviarla.
Una volta avviata è necessario fare il login, alcuni account disponibili sono:

- Username: ”michele”, Password: ”password” 
- Username: ”stefano”, Password: ”password” 
- Username: ”marco”, Password: ”password” 
- Username: ”nicola”, Password: ”password” 
- Username: ”giulia”, Password: ”password”
- Username: ”anna”, Password: ”password”
- Username: ”paolo”, Password: ”password” 
- Username: ”simone”, Password: ”password”

Una volta loggati si potrà scegliere la sorgente e la destinazione del viaggio, attendere la proposta di un veicolo e infine
confermare la prenotazione attendendo l'arrivo del veicolo.

Per quanto riguarda il backoffice è necessario avviare il file "wedriveu-backoffice-xxx.jar", inserire un id
di esempio e a quel punto si potranno vedere i dati dei veicoli.

### Documentazione
 * BackOffice
    - [Java](https://nlasagni.github.io/S3-16-we-drive-u/java/:backoffice/)
 * Mobile
    - [Java](https://nlasagni.github.io/S3-16-we-drive-u/java/:mobile/)
 * Vehicle
    - [Java](https://nlasagni.github.io/S3-16-we-drive-u/java/:vehicle/)
    - [Scala](https://nlasagni.github.io/S3-16-we-drive-u/scala/:vehicle/)
 * Analytics-Service
    - [Java](https://nlasagni.github.io/S3-16-we-drive-u/java/:services:analytics/)
    - [Scala](https://nlasagni.github.io/S3-16-we-drive-u/scala/:services:analytics/)
 * Authentication-Service
    - [Java](https://nlasagni.github.io/S3-16-we-drive-u/java/:services:authentication/)
 * Booking-Service
    - [Java](https://nlasagni.github.io/S3-16-we-drive-u/java/:services:booking/)
    - [Scala](https://nlasagni.github.io/S3-16-we-drive-u/scala/:services:booking/)
 * Vehicle-Service
    - [Java](https://nlasagni.github.io/S3-16-we-drive-u/java/:services:vehicle/)

### Developers
 * Marco Baldassarri
 * Stefano Bernagozzi
 * Michele Donati
 * Nicola Lasagni
 
 