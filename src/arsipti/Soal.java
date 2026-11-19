package arsipti;

import java.util.Set;

public class Soal extends Dokumen {

    private final String jenisUjian;

    public Soal(String judul, String kodeMatkul, int tahun, double ukuranMb, Set<String> kataKunci, String jenisUjian) {
        super(judul, kodeMatkul, tahun, ukuranMb, kataKunci);
        this.jenisUjian = jenisUjian == null ? "" : jenisUjian.trim().toUpperCase();
    }

    @Override
    public void validasi() throws ArsipException {
        super.validasi();
        if (!jenisUjian.equals("UTS") && !jenisUjian.equals("UAS")) {
            throw new ArsipException("Soal wajib mencamtukan jenis ujian (UTS atau UAS).");
        }
    }

    @Override 
    public String buatKodeArsip() {
        return String.format("SOAL-%s-%d-%s", getKodeMatkul().toUpperCase(), getTahun(), jenisUjian );
    }

    @Override
    public String getKategori() {
        return "soal";
    }

    public String getJenisUjian() { return jenisUjian; }
}