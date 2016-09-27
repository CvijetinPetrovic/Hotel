import java.sql.Connection;
import java.sql.DriverManager;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Hotel {
	static Scanner input = new Scanner(System.in);
	static Admin admin = new Admin(); // instanca Admin klase
	static Korisnik korisnik = new Korsinik();// instanca Korisnik klase
	static boolean loop = true; // boolean promjeljiva loop koja nam koristi za
								// petlje

	public static void main(String[] args) throws Exception {
		System.out.println("########################################");
		loop = true;
		while (loop) {
			System.out.print("Dobrodošli. Prijavljujete se kao: \n1.Admin\n2.Gost hotela\n3.Ugasi racunar\n\nBirate: ");
			int izbor = checkInput(3);
			switch (izbor) {
			case 1:
				adminHotela();
				break;
			case 2:
				gost();
				break;
			case 3:
				System.exit(1);
			}
		}

	}

	// opcije za gosta
	public static void gost() throws Exception {
		System.out.println("########################################");
		loop = true;
		String username = null, password = null;
		while (loop) {
			System.out.print("Unesite username: "); // uzimamo username
			username = input.next();
			System.out.print("Unesite password: ");// i password od gosta
			password = input.next();
			if (korisnik.checkUser(username, password)) {// provjeravamo da li
															// su validni
				loop = false;
			} else {
				System.out.println("Wrong username or password, try again: ");
			}
		}
		loop = true;
		while (loop) {
			System.out.println("########################################");
			System.out.print("1.Rezervisi\n2.Promjeni sobu\n3.Odjavi se iz hotela\n4.Izloguj se\n\n Birate: ");
			int izbor = checkInput(4);
			switch (izbor) {
			case 1:
				korisnik.reservation(username);// korisnik pise rezervacije
				break;
			case 2:
				korisnik.updateRoom(username);// korisnik mjenja sobu u hotelu
				break;
			case 3:
				korisnik.checkOutUser(username);// korisnik daje zahtjev za
												// odjavu iz hotela
				break;
			case 4:
				loop = false;
				break;
			}
		}

	}

	// opcije za admina
	public static void adminHotela() throws Exception {
		System.out.println("########################################");
		loop = true;
		while (loop) {
			System.out.print("Unesite username: ");
			String username = input.next();
			System.out.print("Unesite password: ");
			String password = input.next();
			// provjerava username i password admina
			if ((username.equals(admin.getUsername())) && password.equals(admin.getPassword())) {
				loop = false;
			} else {
				System.out.println("Wrong username or password, try again: ");
			}
		}
		loop = true;
		while (loop) {
			System.out.println("########################################");
			System.out.print(
					"1.Dodaj gosta\n2.Rezervacije\n3.Promjeni sobu gosta\n4.Omoguci uslugu\n5.Izdaj racun i odjavi\n6.Izloguj se\n\nBirate: ");
			int izbor = checkInput(4);
			switch (izbor) {
			case 1:
				System.out.println("########################################");
				admin.registrujKorisnika();// registruje novog korisnika
				break;
			case 2:
				System.out.println("########################################");
				admin.rezervacije();// provjerava rezervacije
				break;
			case 3:
				System.out.println("########################################");
				String korisnickoIme = null;
				while (loop) {
					System.out.println("Unesite korisnicko ime korisnika: ");
					korisnickoIme = input.next();
					if (admin.korisnickoPostoji(korisnickoIme)) // provjerava da
																// li korisniko
																// postoji
						loop = false;
				}
				loop = true;
				admin.promjeniSobu(korisnickoIme);// mjenja sobu korisnika
				break;
			case 4:
				System.out.println("########################################");
				String korisnickoIme2 = null;
				while (loop) {
					System.out.println("Unesite korisnicko ime korisnika: ");
					korisnickoIme2 = input.next();
					if (admin.korisnickoPostoji(korisnickoIme2))
						loop = false;
				}
				loop = true;
				admin.odobri(korisnickoIme2, usluga());// odobrava usluge hotela
				break;
			case 5:
				System.out.println("########################################");
				System.out.println("Unesite korisnicko ime gosta: ");
				String ime = input.next();
				admin.izdajRacun(ime);// izdaje racun
				admin.odjaviKorisnika(ime);// odjavljuje korisnika
				break;
			case 6:
				loop = false;
				break;
			}
		}
	}

	// admin bira uslugu i vraca je kao string
	public static String usluga() {
		System.out.println("########################################");
		System.out.print(
				"1.Teretana\n2.Kino\n3.Bazen\n4.Sauna\n5.Teniski teren\n6.Koktel bar\n7.Noæni bar\n8.Korak nazad\n\nBirate: ");
		int izbor = checkInput(5);
		switch (izbor) {
		case 1:
			return "teretana";
		case 2:
			return "kino";
		case 3:
			return "bazen";
		case 4:
			return "sauna";
		case 5:
			return "teniski teren";
		case 6:
			return "koktel bar";
		case 7:
			return "nocni bar";
		case 8:
			break;
		}
		return null;
	}

	// creates connection with database
	public static Connection getConnection() {
		try {
			String user = "root";
			String pass = "perlbak";
			String url = "jdbc:mysql://localhost:3306/hotel?autoReconnect=true&useSSL=false";
			Connection conn = DriverManager.getConnection(url, user, pass);
			return conn;
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	/* provjerava input za godine i broj sobe */
	public static int checkInput(int call) {

		int num = 0;
		boolean error = true; // check for error

		do {
			try {
				num = input.nextInt();
				if (num < 1)
					throw new InputMismatchException("Can not be less than zero, try again: ");
				else if (num > 50 && call == 1)
					throw new InputMismatchException("Can not be larger than 50, try again: ");
				else if (num > 3 && call == 3)
					throw new InputMismatchException("wrong input, try again: ");
				else if (num > 4 && call == 4)
					throw new InputMismatchException("wrong input, try again: ");
				else if (num > 8 && call == 5)
					throw new InputMismatchException("wrong input, try again: ");
				// if input is correct stops loop
				error = false;
			} catch (InputMismatchException e) {
				e.printStackTrace();
				input.nextLine();
			}
		} while (error);

		return num;
	}
}