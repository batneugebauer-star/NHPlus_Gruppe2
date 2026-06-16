# Abgaben

Hier tragt ihr eure Ergebnisse zu den fünf Arbeitsblättern ein. Für jeden AB gibt es eine eigene Markdown-Datei mit vorbereitetem Template.

## Was liegt hier?

| Datei | Inhalt |
|---|---|
| `AB01.md` | Architektur-Analyse (DAO, MVP, Singleton) |
| `AB02.md` | DSGVO-Verstöße und technische Anforderungen |
| `AB03.md` | User Stories, Akzeptanzkriterien, Tasks, Testfälle |
| `AB04.md` | Workstream-Zuordnung, Leseauftrag, Abweichungen |
| `AB05.md` | Testprotokolle, JUnit-Tests, Retrospektive |

Die Aufgabenstellungen selbst liegen unverändert in `../doc/`. Jede Abgabe-Datei verlinkt am Kopf auf ihre Aufgabenstellung.

## Wie füllen?

1. **Meta-Header ausfüllen** — Gruppenname und Mitglieder einmal pro Datei eintragen.
2. **Reihenfolge** — AB 01 → AB 05, jeder AB baut auf dem vorherigen auf.
3. **Unter den Überschriften antworten** — Platzhalter wie `_Eure Antwort hier._` und leere Tabellenzeilen sind zum Ausfüllen gedacht.
4. **Tabellen** — neue Zeilen hinzufügen, wo nötig (User Stories, Testfälle, Anforderungen).

## Git-Workflow

- Bis einschließlich AB 03 reicht es, wenn ihr gemeinsam auf `main` arbeitet.
- Ab **AB 04** arbeitet ihr mit Feature-Branches pro Workstream (siehe AB 04, Abschnitt „Git-Workflow"). Die Abgabe-Datei `AB04.md` kann euer gemeinsamer Sammelpunkt sein — merged ihn rechtzeitig.

## Bewertung

Wie bewertet wird, steht im [Bewertungsraster](../doc/Bewertungsraster.md). Die Gewichtungen pro AB stehen dort.



## Kommentare der Gruppe

Das in User Story 3 angesprochene Event Log wäre eher eine eigene User Story wert gewesen, da es doch recht umfangreich ist. Deshalb funktionieren die darauf aufbauenden Testfälle der Story in der Abgabeversion des Programms nicht.
Die in User Story 3 angesprochenen Sonderspalten zu Änderungszeitpunkt und -Rolle wurden aus zeitgründen nicht mehr umgesetzt, da sie abhängig vom Rollensystem und damit vom Loginsystem sind. Das Rollensystem wurde aufgrund eines Krankheitsfalls erst sehr spät umgesetzt.
In USer Story 4 Entfallen die Testfälle 3 und 6, da die entsprechenden Fenster, und Buttons für nicht berechtigte User nicht angezeigt werden.
In User Story 5 Entfällt aufgrund von Zeitmangel der Testfall 3, da die Rollenberechtigungen zur Zeit so implementiert sind, dass ein NICHT Admin seine Rolle beim Login selbst bestimmt.
User Story 5 ist durch die Verschlüsselung und Loginberechtigungen obsolet geworden. Weitere Testfälle die hier nicht begründet sind, sind auch als Issue im Repository auf Github dokumentiert.

Im Verlauf der Umsetzung wurde das Login-System um eine zusätzliche Verschlüsselungs- und Rollenfunktion erweitert, die über die ursprünglichen Anforderungen aus AB04 hinausgeht. Dabei wurde zu Passwort-Hash und Salt zusätzlich ein verschlüsselt gespeicherter Datenschlüssel je Benutzer eingeführt, der beim Login mit dem Passwort entschlüsselt und während der Session gehalten wird. Außerdem wählen Nicht-Admin-Benutzer beim Login eine Arbeitsrolle und bestätigen diese mit einem zusätzlichen Passwort.
Diese Erweiterung ist im Team organisch entstanden, weil sich die Login-Logik und eine parallel entwickelte DSGVO-Verschlüsselungs-Story inhaltlich überschneiden — beide brauchen denselben Anmeldepunkt. Bei der gemeinsamen Reflexion sind uns dabei drei Punkte aufgefallen, die wir vor der finalen Abgabe noch klären wollen:
Die Rollen-Passwörter liegen aktuell als Klartext im Quellcode. Wir wollen prüfen, ob wir sie stattdessen wie die Login-Passwörter gehasht in der Datenbank speichern.
Beim Zurücksetzen eines Passworts wird aktuell der Verschlüsselungsschlüssel der durchführenden (Admin-)Person verwendet statt der Schlüssel der Zielperson. Das wollen wir gemeinsam nochmal nachvollziehen.
Die neue Funktionalität ist direkt in die bestehenden Login-Klassen eingebaut worden, statt sie in eigene Klassen auszulagern. Eine sauberere Trennung nach dem Single-Responsibility-Prinzip wäre hier rückblickend sinnvoll gewesen.
Aufgrund krankheitsbedingter Ausfälle im Team konnte nicht jede Methode, die im Rahmen dieser Erweiterung entstanden ist, im gleichen Umfang mit Javadoc dokumentiert werden wie der ursprüngliche Login-Workstream. Wir haben uns daher entschieden, zunächst die die Methoden vollständig zu dokumentieren, die wir selbst geschrieben oder vollständig nachvollzogen haben und werden die Dokumentation der übrigen Methoden nachziehen, sobald die personelle Verfügbarkeit es zulässt.
Wir sehen das insgesamt als guten Lernpunkt für die Code-Review im Team: Funktionsüberschneidungen zwischen Workstreams früher abzusprechen, bevor mehrere Personen an derselben Klasse arbeiten, und Dokumentation kontinuierlich statt erst am Ende zu pflegen.


Passworter:
admin: admin123;
pflege: pflege1;
für alle rollen:
    "rollenname": "rollenname123";