package arsipti;

import java.util.Set;

public class Langkah4Uji {
    static void coba(Arsip a, String idUser, Dokumen d, String label) {
        try {
            String id = a.unggah(idUser, d);
            System.out.println("OK    " + label + " -> " + id + " (" + d.getStatus() + ", " + d.buatKodeArsip() + ")");
        } catch (ArsipException e) {
            System.out.println("TOLAK " + label + " -> " + e.getMessage());
        }
    }

    public static void main(String[] args) throws ArsipException {
        Arsip a = new Arsip();
        for (String m : new String[]{"dasar_pemrograman", "algoritma", "pbo", "pemrograman_lanjut"}) {
            a.tambahKodeMatkul(m);
        }
        a.daftarkanPengguna(new Pengguna("k001", "Raka", "kontributor", Set.of()));
        a.daftarkanPengguna(new Pengguna("m001", "Dina", "mahasiswa", Set.of("pbo")));
        try {
            a.daftarkanPengguna(new Pengguna("K001", "Ganda", "admin", Set.of()));
        } catch (ArsipException e) {
            System.out.println("TOLAK daftar ganda -> " + e.getMessage());
        }

        coba(a, "k001", new Materi("Modul PBO", "pbo", 2025, 4.0, Set.of("pbo", "oop"), "modul", 3), "modul PBO");
        coba(a, "k001", new Soal("Soal UTS Algoritma", "algoritma", 2024, 1.5, Set.of("uts"), "UTS"), "soal UTS");
        coba(a, "k001", new Materi(" modul pbo ", "PBO", 2025, 4.0, Set.of(), "modul", 4), "duplikat modul PBO");
        coba(a, "k001", new Materi("Modul Java", "java", 2025, 4.0, Set.of(), "modul", 1), "matkul tidak dikenal");
        coba(a, "x999", new Materi("Modul Lain", "pbo", 2025, 4.0, Set.of(), "modul", 1), "pengguna tidak terdaftar");
        coba(a, "k001", new Materi("Modul Lama", "pbo", 2010, 4.0, Set.of(), "modul", 1), "tahun tidak valid");
        coba(a, "k001", new ReferensiTA("Sistem Presensi Berbasis QR", "pemrograman_lanjut", 2023, 6.0, Set.of("presensi"), "", ""), "TA tanpa pembimbing");
        coba(a, "k001", new ReferensiTA("Sistem Presensi Berbasis QR", "pemrograman_lanjut", 2023, 6.0, Set.of("presensi"), "Dr. Budi", ""), "TA dengan pembimbing");

        System.out.println("jumlah dokumen = " + a.jumlahDokumen());
        System.out.println("cariId(D03) = " + a.cariId("D03").getJudul());
    }
}