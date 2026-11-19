package arsipti;

import java.util.Set;

public class Materi extends Dokumen {

    private final String jenisMateri;
    private final int minggu;

    public Materi(String judul, String kodeMatkul, int tahun, double ukuranMb, Set<String> kataKunci, String jenisMateri, int minggu) {
        super(judul, kodeMatkul, tahun, ukuranMb, kataKunci);
        this.jenisMateri = jenisMateri == null ? "" : jenisMateri.trim().toLowerCase();
        this.minggu = minggu;
    }

    @Override 
    public void validasi() throws ArsipException {
        super.validasi();
        if (!jenisMateri.equals("modul") && !jenisMateri.equals("ppt")) {
            throw new ArsipException("jenisMateri harus 'modul' atau 'ppt'. ");
        }
        if (minggu <1 || minggu > 16) {
            throw new ArsipException("Minggu harus antara 1 dan 16.");
        }
    }
    @Override 
    public String buatKodeArsip() {
        return String.format("MTR-%s-%d-M%02d", getKodeMatkul().toUpperCase(), getTahun(), minggu);
    }

    @Override 
    public String getKategori() {
        return jenisMateri;
    }

    public String getJenisMateri() { return jenisMateri; }
    public int getMingu() { return minggu; }
}