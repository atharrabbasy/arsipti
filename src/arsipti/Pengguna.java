package arsipti;

import java.util.HashSet;
import java.util.Set;

public class Pengguna implements Divalidasi {

    public static final String MAHASISWA = "mahasiswa";
    public static final String KONTRIBUTOR = "kontributor";
    public static final String ADMIN = "admin";

    private final String id;
    private final String nama;
    private final String peran;
    private Set<String> matkulDiambil = new HashSet<>();
    private final Set<String> riwayatUnduh = new HashSet<>();

    public Pengguna(String id, String nama, String peran, Set<String> matkulDiambil) {
        this.id = id == null ? "" : id.trim().toLowerCase();
        this.nama = nama == null ? "" : nama.trim();
        this.peran = peran == null ? "" : peran.trim().toLowerCase();
        if (matkulDiambil != null) {
            for (String m : matkulDiambil) {
                if (m != null && !m.isBlank()) {
                    this.matkulDiambil.add(m.trim().toLowerCase());
                }
            }
        }
    }
    
    @Override 
    public void validasi() throws ArsipException {
        if (id.isEmpty()) {
            throw new ArsipException("ID pengguna tidak boleh kosong.");
        }
        if (nama.isEmpty()) {
            throw new ArsipException("Nama pengguna tidak boleh kososng.");
        }
        if (!peran.equals(MAHASISWA) && !peran.equals(KONTRIBUTOR) && !peran.equals(ADMIN)) {
            throw new ArsipException("Peran harus mahasiswa, kontributor, atau admin.");
        }
    }

    public boolean bolehModerasi() {
        return peran.equals(ADMIN);
    }

    void catatUnduh(String idDokumen) {
        riwayatUnduh.add(idDokumen);
    }

    public String getId() { return id; }
    public String getNama() { return nama; }
    public String getPeran() { return peran; }
    public Set<String> getMatkulDiambil() { return matkulDiambil; }
    public Set<String> getRiwayatUnduh() { return riwayatUnduh; }
}