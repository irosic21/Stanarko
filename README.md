# Stanarko

## Opis domene
Aplikacija će biti namijenjena stanodavcima i stanarima tih objekata. Aplikacije će omogućiti stanodavcima unošenje novih stanova. Aplikacija će također omogućiti stanodavcima i stanarima objekata upravljanjem računima stanarine, te prijavom kvarova i slanjem obavijesti o remećenju javnog mira.

## Specifikacija projekta
Oznaka | Naziv | Kratki opis | Odgovorni član tima
------ | ----- | ----------- | -------------------
F01 | Registracija | Korisnik će se morati registrirati kako bi mogao pristupiti sustavu. | Ivo Rošić
F02 | Prijava | Da bi korisnik mogao koristiti aplikaciju mora se prvo prijaviti u sustav. | Ivo Rošić
F03 | Unos i brisanje stanova | Sustav će omogućiti dodavanje i brisanje novih stanova na popis dostupnih. | Danijel Žebčević
F04 | Generiranje računa | Stanodavac će moći kreirati račun kojeg stanari moraju platiti. | Matija Tomašić
F05 | Plaćanje računa | Nakon što stanodavac kreira račun stanar će moći platiti račun pomoću sustava. | Matija Tomašić
F06 | Grafički prikaz rezervacija |Sustav će stanodavcu, omogućiti prikaz stanja svakog stana (zauzet ili slobodan), te broj stanara unutar svakog stana. | Robert Vrđuka
F07 | Dodavanje ljudi u stan | Stanodavac će moći dodati nove stanare u svoj stan. | Danijel Žebčević
F08 | Planiranje useljenja | Sustav će omogućiti korisniku da rezervira datum kad se želi useliti u stan. | Danijel Žebčević
F09 | Aplikacijski chat između stanodavca i podstanara | Stanar i stanodavac će moći komunicirati preko chat-a unutar same aplikacije | Matija Tomašić
F10 | Obavještavanje kvarova | Vlasnik će dobiti "push" notifikaciju kada je prijavljen kvar u njegovom stanu. | Robert Vrđuka
F11 | Kreiranje ugovora | Sustav će omogućiti kreiranje ugovora između stanodavca i stanara. | Ivo Rošić
F12 | Prijava kvarova | U slučaju da korisnik ima kvar u stanu, moći će ga prijaviti u aplikaciji. | Robert Vrđuka
F13 | Mijenjanje statusa kvarova | Nakon što se prijavljeni kvar popravi, stanodavac će ga moći označiti kao popravljenim. | Danijel Žebčević
F14 | Planiranje selidbe | Aplikacija će omogućiti stanarima da najave svoje iseljenje iz stana, te će zatražiti za koliko dana se stanar planira iseliti (minimalno 30 dana) | Ivo Rošić


## Tehnologije i oprema
Za aplikaciju ćemo koristiti razvojno okruženje Android Studio te programski jezik Kotlin. Za verzioniranje koda te dokumentaciju koristit ćemo platformu Github.

## Baza podataka i web server
Trebamo bazu podataka i pristup serveru za PHP skripte

## .gitignore
Uzmite u obzir da je u mapi Software .gitignore konfiguriran za nekoliko tehnologija, ali samo ako će projekti biti smješteni direktno u mapu Software ali ne i u neku pod mapu. Nakon odabira konačne tehnologije i projekta obavezno dopunite/premjestite gitignore kako bi vaš projekt zadovoljavao kriterije koji su opisani u ReadMe.md dokumentu dostupnom u mapi Software.
