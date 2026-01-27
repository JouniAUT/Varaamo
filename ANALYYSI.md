# Analyysi tekoälyn tuottamasta koodista

## Mitä tekoäly teki hyvin?

Tekoäly pystyi rakentamaan sovelluksen puitteet äärettömän nopeasti valituilla teknologioilla. Tässä mielestäni etuna on se, että projekti saa hyvin nopeasti käyttöönsä kehitysympäristön, jossa on yhteensopivat backend ja frontend. Ne myös toimivat heti alusta lähtien saumattomasti yhdessä.

Lisäksi tekoäly pystyi huolehtimaan datan eheydestä erittäin taidokkaasti, koska sen toimiin sisältyi automaattisesti esimerkiksi @Transactional- ja @Valid-beanien käyttö.

Tekoäly pystyi myös ottamaan tulevaisuudessa vaadittavia ominaisuuksia huomioon ehdotuksissaan. Esimerkiksi skaalautuvuuden ja turvallisuuden näkökulma oli aina jatkokysymyksissä läsnä, mikä on mielestäni hyvä asia.

Myös nimeäminen oli laadultaan hyvää. Uskon, että valintani käyttää englantia tuki tekoälyä ja koodin luettavuutta huomattavasti.

Tekoälyn kyky analysoida valittujen tiedostojen koodia nopeasti on myös taito, jonka se pystyy suorittamaan erinomaisesti.

Oma lähestymistapani oli se, että tekoäly saa tehdä vain asioita, joita siltä pyydän ja joihin annan sille luvan. Se toimi siinä mielessä hyvin, että sovellus ei päässyt kasvamaan liian isoksi yhdellä kertaa ja kokonaisuus pysyi hallittavana ja helposti analysoitavana sekä seurattavana. Kokemukseni mukaan tekoäly hyödyntää tulkinnanvaraiset promptit liian liberaalisti ja lähtee helposti rakentamaan isoa kokonaisuutta, jonka muokkaaminen omiin tarpeisiin sopivaksi voi olla myöhemmin todella työlästä.

Kaiken kaikkiaan koen, että tekoälyn vahvuus oli backendin rakentamisessa, jossa loogisuus, validointi ja selkeästi määritellyt asiat ovat tärkeässä roolissa. Backend on rakenteeltaan ja luonteeltaan vahvasti määritelty ja se nojaa protokolliin ja logiikkaan, jota tekoälyn on helppo ymmärtää ja analysoida.

## Mitä tekoäly teki huonosti?

Koen, että suurimmat ongelmat tekoälyllä olivat UI:hin liittyvissä yksityiskohdissa.

Tekoälyn käyttämä päivä- ja aikavalitsin oli sellaisenaan käyttäjälle mielestäni hieman kömpelö. Myös alkuperäinen navigointipalkki ei toiminut erityisen hyvin, koska tabien värit katosivat välillä taustaan.

Lisäksi oli muutama muukin ongelma, jotka olivat mielestäni huomioimisen arvoisia.
Esimerkiksi huoneiden koko ja osallistujien lukumäärä olivat asioita, joita AI ei huomioinut omassa raakaversiossaan. Annoin myös projektin alussa ohjeen, jonka mukaan UI on suomeksi, mutta siitä huolimatta käyttäjille päätyi joitain englanninkielisiä virheviestejä.

Näistä ongelimista huolimatta tekoäly loi hyvän perusrakenteen, jota oli helppo lähteä jatkokehittämään haluttuun suuntaan.

Vaikka tekoäly pystyi luomaan nopeasti frontendille puitteet, suurin haaste sille oli frontendin ihmiskeskeisyys. Tekoälyn oli selvästi vaikea kehittää asioita, joita koetaan ja käytetään ihmiskäyttäjän aisteilla.

## Mitkä olivat tärkeimmät parannukset, jotka teit tekoälyn tuottamaan koodiin ja miksi?

Aivan ensimmäiseksi siirsin backendin tiedostot luomaani backend-kansioon, mikä toi mielestäni projektille selkeän rakenteen ja teki siitä helpommin navigoitavan.

Tämän jälkeen lisäsin kapasiteetti- ja osallistujamääräattribuutit, joita tekoäly ei ollut lisännyt sovellukseen. Lisäsin myös näihin attribuutteihin nojaavat toiminnallisuudet. Näitä toiminnallisuuksia olivat osallistujamäärän valinta sekä siihen perustuva huoneiden suodatus.Mielestäni ne olivat erittäin tärkeitä sovelluksen totuudenmukaisuuden ja käytettävyyden kannalta.

Lisäsin myös kehitysdataa, joka lisätään aina, kun backend käynnistyy, jotta kehitystyö on sujuvaa. Vaikka projekti ei todennäköisesti etene julkaisuvaiheeseen, halusin silti tehdä siitä mahdollisimman aidon ja lisäsin @Profile-suojauksen, jolla pidän huolen, että kehitysdata ei siirry sovelluksen julkaistuun versioon.

Korjasin myös aiemmin mainittuja UI-ongelmia, joilla pystyin parantamaan käyttäjäkokemusta miellyttävämpään suuntaan. Muokkasin aikavalitsimen tekoälyn avustuksella niin, että varauksen alkamisaika on 15 minuutin välein. Tekoäly oli asettanut oletukseksi tämänhetkisen ajan, ja varauksia pystyi tekemään jokaiselle minuutille.

Paransin myös navigaatiopalkin näkymää, jossa valittu toiminto katosi palkkiin, koska teksti sulautui palkkiin.

