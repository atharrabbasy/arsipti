package arsipti;

import java.util.List;
import java.util.Set;

public class Langkah5uji {
    static void tampil(String label, List<Dokumen> hasil) {
        if (hasil.isEmpty()) {
            System.out.println(label + " -> tidak ada dokumen yang sesuai");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Dokumen d : hasil) {
            sb.append(d.getId()).append("(").append(d.getTahun()).append(") ");
        }
        System.out.println(label + " -> " + sb.toString().trim());
    }

    static void moderasi(Arsip a, String user, String dok, boolean setuju) {
        try {
            a.moderasi(user, dok, setuju);
            System.out.println("OK    " + user + (setuju ? " setujui " : " tolak ") + dok + " -> " + a.cariId(dok).getStatus());
        } catch (ArsipException e) {
            System.out.println("TOLAK " + user + " moderasi " + dok + " -> " + e.getMessage());
        }
    }

    static void unduh(Arsip a, String user, String dok) {
        try {
            a.unduh(user, dok);
            System.out.println("OK    " + user + " unduh " + dok + " -> jumlahUnduh = " + a.cariId(dok).getJumlahUnduh());
        } catch (ArsipException e) {
            System.out.println("TOLAK " + user + " unduh " + dok + " -> " + e.getMessage());
        }
    }

    public static void main(String[] args) throws ArsipException {
        Arsip a = new Arsip();
        for (String m : new String[]{"algoritma", "pbo", "pemrograman_lanjut"}) a.tambahKodeMatkul(m);
        a.daftarkanPengguna(new Pengguna("m001", "Dina", "mahasiswa", Set.of("pbo")));
        a.daftarkanPengguna(new Pengguna("k001", "Raka", "kontributor", Set.of()));
        a.daftarkanPengguna(new Pengguna("a001", "Bu Sari", "admin", Set.of()));

        a.unggah("k001", new Materi("Modul PBO", "pbo", 2025, 4.0, Set.of("pbo", "oop"), "modul", 3)); // d01
        a.unggah("k001", new Materi("PPT PBO", "pbo", 2024, 2.5, Set.of("pbo"), "ppt", 1));            // d02
        a.unggah("k001", new Soal("Soal UAS Algoritma", "algoritma", 2025, 1.5, Set.of("uas"), "UAS")); // d03
        a.unggah("k001", new ReferensiTA("Sistem Presensi Berbasis QR", "pemrograman_lanjut", 2023, 6.0,
                Set.of("presensi"), "Dr. Budi", ""));                                                  // d04

        tampil("cari 'pbo' sebelum moderasi", a.cari("pbo", "", 0));
        moderasi(a, "m001", "d01", true);
        moderasi(a, "a001", "d99", true);
        moderasi(a, "a001", "d01", true);
        moderasi(a, "a001", "d01", true);
        moderasi(a, "a001", "d02", true);
        moderasi(a, "a001", "d03", true);
        moderasi(a, "a001", "d04", false);

        tampil("cari 'pbo'", a.cari("pbo", "", 0));
        tampil("cari 'presensi' (ditolak)", a.cari("presensi", "", 0));
        tampil("cari 'PBO' kategori ppt", a.cari("PBO", "ppt", 0));
        tampil("cari '' tahun >= 2025", a.cari("", "", 2025));
        tampil("cari kosong semua", a.cari("", "", 0));
        tampil("cari 'java'", a.cari("java", "", 0));

        unduh(a, "m001", "d01");
        unduh(a, "m001", "d01");
        unduh(a, "m001", "d04");
        unduh(a, "m001", "d77");
        System.out.println("riwayat m001 = " + a.cariPengguna("m001").getRiwayatUnduh());
        System.out.println("jumlahUnduh d04 = " + a.cariId("d04").getJumlahUnduh());
    }
}