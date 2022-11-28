package pl.sda.hibernate;

import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import pl.sda.hibernate.model.Pojazd;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Podaj komende (dodaj / lista / szukaj / usun / aktualizuj):");
        String komenda = scanner.nextLine();

        if (komenda.equalsIgnoreCase("dodaj")) {
            obslugaDodaj(scanner);
        } else if (komenda.equalsIgnoreCase("lista")) {
            obslugaLista();
        } else if (komenda.equalsIgnoreCase("szukaj")) {
            obslugaSzukaj(scanner);
        } else if (komenda.equalsIgnoreCase("usun")) {
            obslugaUsun(scanner);
        } else if (komenda.equalsIgnoreCase("aktualizuj")) {
            obslugaAktualizuj(scanner);
        }
    }

    public static void obslugaDodaj(Scanner scanner) {
        Pojazd p = wczytajParametryPojazdu(scanner);

        try (Session session = HibernateUtil.INSTANCE.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.persist(p);

            transaction.commit();
        } catch (Exception e) {
            System.err.println("Błąd dodawania do bazy danych");
        }
    }

    public static Pojazd wczytajParametryPojazdu(Scanner scanner) {
        System.out.println("Podaj marke");
        String marka = scanner.nextLine();

        System.out.println("Podaj moc");
        String mocString = scanner.nextLine();
        Double moc = Double.parseDouble(mocString);

        System.out.println("Podaj kolor");
        String kolor = scanner.nextLine();

        Integer rokProdukcji = null;
        do {
            System.out.println("Podaj rok produkcji:");
            String rokProdukcjiString = scanner.nextLine();
            rokProdukcji = Integer.parseInt(rokProdukcjiString);

            if (rokProdukcji < 1990 || rokProdukcji > 2020) {
                // ustawiamy null by wymusić powtórzenie pętli
                rokProdukcji = null;
            }
        } while (rokProdukcji == null);

        System.out.println("Czy jest elektryczny");
        String elektrycznyString = scanner.nextLine();
        boolean elektryczny = Boolean.parseBoolean(elektrycznyString);

        return Pojazd.builder()
                .marka(marka)
                .moc(moc)
                .kolor(kolor)
                .rokProdukcji(rokProdukcji)
                .elektryczny(elektryczny)
                .build();
    }

    public static void obslugaLista() {
        try (Session session = HibernateUtil.INSTANCE.getSessionFactory().openSession()) {
            TypedQuery<Pojazd> zapytanie = session.createQuery("FROM Pojazd", Pojazd.class);
            List<Pojazd> listaWszystkichPojazdow = zapytanie.getResultList();

            // najkrotsza
//            lista.forEach(System.out::println);

            // najkrotsza - 1
//            lista.forEach((paramPojazd) -> {
//                System.out.println(paramPojazd);
//            });

            // najkrotsza - 2
//            lista.forEach(new Consumer<Pojazd>() {
//                @Override
//                public void accept(Pojazd paramPojazd) {
//                    System.out.println(paramPojazd);
//                }
//            });

            // prymitywna
            for (Pojazd pojazd : listaWszystkichPojazdow) {
                System.out.println(pojazd);
            }
        } catch (Exception ioe) {
            // jeśli złapiemy błąd, to wywoła się catch
            System.err.println("Błąd bazy: " + ioe);
        }
    }

    public static void obslugaSzukaj(Scanner scanner) {
        System.out.println("Podaj identyfikator szukanego obiektu:");
        String identyfikator = scanner.nextLine();
        Long id = Long.parseLong(identyfikator);

        try (Session session = HibernateUtil.INSTANCE.getSessionFactory().openSession()) {
            Pojazd pojazd = session.get(Pojazd.class, id);

            if (pojazd == null) {
                System.err.println("Rekord nie istnieje");
            } else {
                System.out.println(pojazd);
            }
        } catch (Exception ioe) {
            System.err.println("Błąd szukania rekordu w bazie danych");
        }
    }

    private static void obslugaUsun(Scanner scanner) {
        System.out.println("Podaj identyfikator usuwanego obiektu:");
        String identyfikator = scanner.nextLine();
        Long id = Long.parseLong(identyfikator);

        try (Session session = HibernateUtil.INSTANCE.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Pojazd pojazd = session.get(Pojazd.class, id);

            if (pojazd == null) {
                System.err.println("Rekord nie istnieje");
            } else {
                session.remove(pojazd);
                System.out.println("Usunięto rekord o identyfikatorze: " + id);
            }
            transaction.commit();
        } catch (Exception ioe) {
            System.err.println("Błąd usuwania rekordu z bazy danych");
        }
    }

    private static void obslugaAktualizuj(Scanner scanner) {
        System.out.println("Podaj identyfikator szukanego obiektu:");
        String identyfikator = scanner.nextLine();
        Long id = Long.parseLong(identyfikator);

        try (Session session = HibernateUtil.INSTANCE.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Pojazd pojazd = session.get(Pojazd.class, id);

            if (pojazd == null) {
                System.err.println("Rekord nie istnieje.");
            } else {
                Pojazd daneAktualizujacePojazd = wczytajParametryPojazdu(scanner);
                daneAktualizujacePojazd.setId(id);

                session.merge(daneAktualizujacePojazd);
            }
            transaction.commit();
        } catch (Exception ioe) {
            System.err.println("Błąd aktualizacji rekordu w bazie danych");
        }
    }
}