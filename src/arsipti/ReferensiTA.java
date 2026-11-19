package arsipti;

import java.util.Set;

public class ReferensiTA extends Dokumen {

    private final String pembimbing;
    private final String abstrak;

    public ReferensiTA(String judul, String kodeMatkul, int tahun, double ukuranMb, Set<String> kataKunci, String pembimbing, String abstrak) {
        super(judul, kodeMatkul, tahun, ukuranMb, kataKunci);
        this.pembimbing = pembimbing == null ? "" : pembimbing.trim();
        this.abstrak = abstrak == null ? "" : abstrak.trim();
    }

    @Override 
    public void validasi() throws ArsipException {
        super.validasi();
        if (pembimbing.isEmpty()) {
            throw new ArsipException("Pembimbing");
        }
    }

    @Override 
    public String buatKodeArsip() {
        return String.format("TA-%s-%d", getKodeMatkul().toUpperCase(), getTahun());

    }

    @Override 
    public String getKategori() {
        return "referensi_ta";
    }

    public String getPembimbing() { return pembimbing; }
    public String getAbstrak() { return abstrak; }

}