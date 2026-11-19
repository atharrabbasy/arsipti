package arsipti;

import java.util.Set;

public class Langkah3Uji {
    static void cek(Pengguna p) {
        try {
            p.validasi();
            System.out.println("OK    " + p.getId() + " (" + p.getPeran() + ") boleh moderasi = " + p.bolehModerasi());
        } catch (ArsipException e) {
            System.out.println("TOLAK " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Pengguna dina  = new Pengguna("M001", "Dina", "Mahasiswa", Set.of("PBO"));
        Pengguna eko   = new Pengguna("m002", "Eko", "mahasiswa", Set.of("dasar_pemrograman"));
        Pengguna raka  = new Pengguna("k001", "Raka", "kontributor", Set.of());
        Pengguna admin = new Pengguna("a001", "Bu Sari", "admin", null);
        cek(dina); cek(eko); cek(raka); cek(admin);

        cek(new Pengguna("m003", "  ", "mahasiswa", Set.of()));
        cek(new Pengguna("m004", "Fani", "dosen", Set.of()));

        System.out.println("matkul Dina  = " + dina.getMatkulDiambil());
        dina.getMatkulDiambil().add("basis_data");   // salinan: tidak mengubah data asli
        System.out.println("setelah add pada salinan = " + dina.getMatkulDiambil());

        dina.catatUnduh("d07");
        dina.catatUnduh("d07");                       // Set: tidak ganda
        System.out.println("riwayat Dina = " + dina.getRiwayatUnduh());
    }
}