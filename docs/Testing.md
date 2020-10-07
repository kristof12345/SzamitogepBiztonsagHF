# Szamitogep Biztonság HF tesztelési terv
## Funkcionális tesztelés
Funkcionális tesztelés során ellenőrizzük, hogy a rendszer specifikációban szereplő és megvalósított funkciói működnek-e. A szerver oldal átfogó teszteléséhez a Postman alkalmazást használjuk, mely segítségével HTTP kéréseket intézhetünk a szerverhez, és megfigyelhetjük a rá adott válaszokat. Az androidos kliens esetében manuális tesztelést alkalmazunk, melyhez a tesztelés korai fázisában egy mock szervert, a későbbiekben pedig az elkészült szervert használjuk fel. Az összetettebb logika ellenőrzésére Unit teszteket készítünk.
A tesztelés során az alábbi, a specifikációban is szereplő use-case-eket próbáljuk ki:
* Felhasználó regisztrációja
* Bejelentkezés a regisztrált felhasználóval
* CAFF fájl feltöltése
* Keresés CAFF fájlok között
* Komment hozzáadása CAFF fájlhoz
* CAFF fájl megvásárlása és letöltése

## Nemfunkcionális tesztelés
A nemfunkcionális tesztelés során azt ellenőrizzük, hogy a rendszerrel szemben nem végezhető olyan viselkedés, amit nem engedtünk meg. A lehetséges hibák felderítéséhez statikus ellenőrzési technikákat alkalmazunk. Ezek közül a manuális code review során egy olyan csapattag nézi át az elkészült kódot, aki az adott modulon / funkción nem dolgozott, de a technológiát alaposan ismeri. A statikus ellenőrzéshez automatizált eszközöket is alkalmazunk, a konkrét programokat később határozzuk meg.
Kitérünk az alábbi scenáriók tesztelésére (annak ellenőrzésére, hogy nem lehet végrehajtani őket) is:
* Nem létező felhasználó bejelentkezése
* Két felhasználó regisztrálása azonos felhasználónévvel
* Bejelentkezés nélküli hozzáférés a rendszerhez
* Adminisztrátori funkciók elérése normál felhasználók számára