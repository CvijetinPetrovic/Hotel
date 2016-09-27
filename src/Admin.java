import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/* Created by Petar Sunaric */

public class Admin {

	private final String username = "admin";
	private final String password = "hotel";
	static Scanner input = new Scanner(System.in);

	Admin() {
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	/** ispisuje rezervacije i nakon toga prazni fajl */
	public void rezervacije() throws FileNotFoundException {

		File file = new File("rezervacije.txt");
		Scanner output = new Scanner(file);
		while (output.hasNextLine()) {
			System.out.println(output.nextLine());
		}
		output.close();
		PrintWriter pw = new PrintWriter(file);
		pw.write(" ");
		pw.close();
	}

	/** odjavi korisnika i postavi sve iskoristive vrijednosti na null */
	public void odjaviKorisnika(String username) {
		String[] kolona = { "teretana", "kino", "bazen", "sauna", "teniski teren", "koktel bar", "nocni klub" };
		try {
			Connection con = Hotel.getConnection();
			for (int i = 0; i < kolona.length; i++) {
				PreparedStatement statement = con.prepareStatement(
						"UPDATE hotel.gost SET " + kolona[i] + "=null WHERE korisnickoIme='" + username + "'");
				statement.executeUpdate();
			}
			PreparedStatement statement2 = con
					.prepareStatement("UPDATE hotel.gost SET soba=null WHERE korisnickoIme='" + username + "'");
			statement2.executeUpdate();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/** promjeni sobu korisniku */
	public void promjeniSobu(String username) throws Exception {
		try {
			Connection con = Hotel.getConnection();
			System.out.println("Unesite novu sobu: ");
			int soba = 0;
			do {
				soba = Hotel.checkInput(1);
			} while (izdata(soba));

			PreparedStatement statement = con.prepareStatement(
					"UPDATE hotel.gost SET soba='" + soba + "'WHERE korisnickoIme='" + username + "'");
			statement.executeUpdate();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * odobrava nekakav zahtjev. unosi se korisnikov username i ime kolone koju
	 * treba da odobri
	 */
	public void odobri(String username, String kolona) throws Exception {
		try {
			Connection con = Hotel.getConnection();
			PreparedStatement statement = con.prepareStatement(
					"UPDATE hotel.gost SET +" + kolona + "='" + "D" + "'WHERE korisnickoIme='" + username + "'");
			statement.executeUpdate();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * izdaje racun sa svim koristenim uslugama, njihovim cijenama i ukupnom
	 * cijenom na zadato korisnicko ime gosta
	 */
	public void izdajRacun(String ime) throws Exception {
		if(korisnickoPostoji(ime)){
		int racun = 0;// ukupan racun
		try {
			Connection con = Hotel.getConnection();
			PreparedStatement statement = con
					.prepareStatement("SELECT * FROM hotel.gost WHERE korisnickoIme='" + ime + "'");
			ResultSet rs = statement.executeQuery();
			int brojSobe = rs.getInt("brojSobe");
			String teretana = rs.getString("teretana");
			String kino = rs.getString("kino");
			String bazen = rs.getString("bazen");
			String sauna = rs.getString("sauna");
			String tenis = rs.getString("teniskiTeren");
			String koktel = rs.getString("koktelBar");
			String klub = rs.getString("nocniKlub");
			String datum = rs.getString("datumPrijave");

			System.out.println("Goste ste prijavio: " + datum);
			System.out.print("Koliko je dana gost bio? ");
			int danaBoravio = Hotel.checkInput(0);
			System.out.println("RACUN: ");
			System.out.println("--------------------------");
			racun += danaBoravio * naplatiSobu(brojSobe);
			System.out.print(danaBoravio + " dana: " + racun + "KM");
			if (koristeno(teretana)) {
				System.out.println("Teretana: 10KM");
				racun += 10;
			}
			if (koristeno(kino)) {
				System.out.println("Kino: 10KM");
				racun += 10;
			}
			if (koristeno(bazen)) {
				System.out.println("Bazen: 15KM");
				racun += 15;
			}
			if (koristeno(sauna)) {
				System.out.println("Sauna: 15KM");
				racun += 15;
			}
			if (koristeno(tenis)) {
				System.out.println("Teniski teren: 30KM");
				racun += 30;
			}
			if (koristeno(koktel)) {
				System.out.println("Koktel bar: 30KM");
				racun += 30;
			}
			if (koristeno(klub)) {
				System.out.println("Nocni klub: 40KM");
				racun += 40;
			}
			System.out.println("--------------------------");
			System.out.println("             ukupno: " + racun);

		} catch (Exception e) {
			System.out.println(e);
		}
		}else{
			System.out.println("Pogresan unos!");
		}
	}

	/* odredjuje cijenu sobe */
	public int naplatiSobu(int soba) {
		if (soba < 21) {
			System.out.print("Jednokrevetna soba, ");
			return 40;
		} else if (soba < 41) {
			System.out.print("Dvokrevetna soba, ");
			return 70;
		}
		System.out.print("Apartman, ");
		return 250;
	}

	/* provjerava da li je usluga koristena */
	public boolean koristeno(String usluga) {
		if (usluga.equals("D"))
			return true;
		return false;
	}

	/** registruje korisnika */
	public void registrujKorisnika() throws Exception {

		System.out.print("Unesite broj licne karte: ");
		String brojLicne = input.next();
		if (daLiPostojiKorisnik(brojLicne)) {
			System.out.print("Unesite broj sobe: ");
			int brojSobe = Hotel.checkInput(1);
			insertSobe(brojSobe);
		} else {

			System.out.print("Unesite ime: ");
			String ime = input.next();

			System.out.print("Unesite prezime: ");
			String prezime = input.next();

			System.out.print("Unesite pol(M ili Z): ");
			String pol = input.next().toUpperCase();

			System.out.print("Unesite godine: ");
			int godine = Hotel.checkInput(0);

			System.out.print("Unesite broj sobe: ");
			int brojSobe = 0;
			do {
				brojSobe = Hotel.checkInput(1);
			} while (izdata(brojSobe));

			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			Date date = new Date();
			String datum = sdf.format(date);

			System.out.print("Unesite korisnicko ime: ");
			boolean loop = true;
			String korisnickoIme = null;
			while (loop) {
				korisnickoIme = input.next();
				if (korisnickoPostoji(korisnickoIme)) {
					System.out.print("Korisnicko ime postoji, izaberite drugo: ");
					// input.nextLine();???????????????????????????
				} else
					loop = false;
			}
			System.out.println("Unesite lozinku: ");
			String lozinka = input.next();

			insertKorisnika(ime, prezime, pol, brojLicne, godine, brojSobe, datum, korisnickoIme, lozinka);
		}
	}

	/* provjerava da li korisnicko ime vec postoji */
	public boolean korisnickoPostoji(String ime) {
		try {
			Connection con = Hotel.getConnection();
			PreparedStatement statement = con
					.prepareStatement("SELECT korisnickoIme FROM hotel.gost WHERE korisnickoIme='" + ime + "'");
			ResultSet result = statement.executeQuery();
			if (result.getString("korisnickoIme").equals(ime))
				return true;
		} catch (Exception e) {
			System.out.println(e);
		}
		return false;
	}

	/* insertuje korisnika u tabelu hotel.gost */
	public void insertKorisnika(String ime, String prezime, String pol, String brojLicne, int godine, int brojSobe,
			String datum, String korisnickoIme, String lozinka) throws Exception {
		try {
			Connection con = Hotel.getConnection();
			PreparedStatement posted = con.prepareStatement(
					"INSERT INTO hotel.gost VALUES ('" + ime + "','" + prezime + "','" + pol + "','" + brojLicne + "',"
							+ godine + "," + brojSobe + ",'" + datum + "','" + korisnickoIme + "','" + lozinka + "')");
			posted.executeUpdate();
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			System.out.println("Korisnik kreiran!");
		}

	}

	/* insertuje broj sobe u tabelu hotel.gost */
	public void insertSobe(int soba) throws Exception {
		boolean loop = true;
		try {
			Connection con = Hotel.getConnection();
			while (loop) {
				if (!izdata(soba)) {
					PreparedStatement posted = con
							.prepareStatement("INSERT INTO hotel.gost(brojSobe) VALUES(" + soba + ")");
					posted.executeUpdate();
					loop = false;
				} else {
					System.out.print("Soba je vec izdata, izaberite drugu: ");
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			System.out.println("Korisnik kreiran!");
		}
	}

	/* vraca true ako je soba izdata */
	public boolean izdata(int soba) throws Exception {
		if (provjeriSobu(soba).equals("D"))
			return true;
		return false;
	}

	/* provjerava da li je soba izdata */
	public String provjeriSobu(int soba) throws Exception {
		try {
			Connection con = Hotel.getConnection();
			PreparedStatement statement = con
					.prepareStatement("SELECT izdata FROM hotel.soba WHERE soba='" + soba + "'");
			ResultSet result = statement.executeQuery();
			return result.getString("izdata");
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	/* provjerava da li je korisnik vec bio u hotelu */
	public boolean daLiPostojiKorisnik(String brojLicne) throws Exception {
		if (brojLicne.equals(getStariBroj(brojLicne)))
			return true;
		return false;
	}

	/* vraca broj licne ako je gost vec bio u hotelu u suprotnom vraca null */
	public String getStariBroj(String brojLicne) throws Exception {
		try {
			Connection con = Hotel.getConnection();
			PreparedStatement statement = con
					.prepareStatement("SELECT brojLicne FROM hotel.gost WHERE brojLicne='" + brojLicne + "'");
			ResultSet result = statement.executeQuery();
			return result.getString("brojLicne");
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	
}