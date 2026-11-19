package arsipti;

import java.util.HashSet;
import java.util.Set;

public abstract class Dokumen implements Divalidasi {

    public enum Status { MENUNGGU, DISETUJUI, DITOLAK }

    public static final int TAHUN_MIN = 2015;
    public static final int TAHUN_MAKS = 2026;
    public static final double UKURAN_MAKS_MB = 20.0;

    private String id;
    private final String judul;
    private final String kodeMatkul;
    private final int tahun;
    private final double ukuranMb;
    private final Set<String> kataKunci = new HashSet<>();
    private Status status = Status.MENUNGGU;
    private int jumlahUnduh = 0;

    protected Dokumen(String judul, String kodeMatkul, int tahun, double ukuranMb, Set<String> kataKunci) {
        this.judul = judul == null ? "" : judul.trim();
        this.kodeMatkul = kodeMatkul == null ? "" : kodeMatkul.trim().toLowerCase();
        this.tahun = tahun;
        this.ukuranMb = ukuranMb;
        if (kataKunci != null) {
            for (String k : kataKunci) {
                if (k != null && !k.isBlank()) {
                    this.kataKunci.add(k.trim().toLowerCase());
                }
            }
        }
    }


    public abstract String buatKodeArsip();
    public abstract String getKategori();

    @Override 
    public void validasi() throws ArsipException {
        if(judul.isEmpty()) {
            throw new ArsipException("Judul tidak boleh kosong.");
        }
        if (kodeMatkul.isEmpty()) {
            throw new ArsipException("Kode matkul tidak boleh kosong.");
        }
        if (tahun < TAHUN_MIN || tahun > TAHUN_MAKS) {
            throw new ArsipException("Tahun harus antara" + TAHUN_MIN + "dan" + TAHUN_MAKS + ".");
        }
        if (ukuranMb <= 0 || ukuranMb > UKURAN_MAKS_MB) {
            throw new ArsipException(("Ukuran dokumen harus leih dari 0 dan maksimal " + (int) UKURAN_MAKS_MB + " MB. "));
        }
    }

    public void setujui() throws ArsipException {
        pastikanMenunggu();
        status = Status.DISETUJUI;
    }

    public void tolak() throws ArsipException {
        pastikanMenunggu();
        status = Status.DITOLAK;
    }

    public void pastikanMenunggu() throws ArsipException {
        if (status != Status.MENUNGGU) {
            throw new ArsipException("Dokumen " + id + " sudah dimoderasi(" +status+ ").");
        }
    }

    public void tambahUnduh() throws ArsipException {
        if (status != Status.DISETUJUI) {
            throw new ArsipException("Dokumen " + id + "belum disetujui, tidak dapat diunduh.");
        }
        jumlahUnduh++;
    }

    public boolean cocokKataKunci(String kata) {
        if (kata == null || kata.isBlank()) {
            return true;
        }
        String k = kata.trim().toLowerCase();
        return  judul.toLowerCase().contains(k) || kataKunci.contains(k);
    }

    public  String getId() { return id; }
    public  String getJudul() { return judul; }
    public  String getKodeMatkul() { return kodeMatkul; }
    public  int getTahun() { return tahun; }
    public  double getUkuranMB() { return ukuranMb; }
    public  Set<String> getKataKunci() { return new HashSet<>(kataKunci); }
    public  Status getStatus() { return status; }
    public  int getJumlahUnduh() { return jumlahUnduh; }

    void tetapkanId(String id) { this.id = id;}

}