package arsipti;

import java.util.List;
import java.util.Set;

public class Langkah6Uji {
    public static void main(String[] args) throws ArsipException {
        Arsip a = new Arsip();

        // arsip kosong: tidak boleh error
        System.out.println("kosong: kategori = " + a.jumlahPerKategori() + ", populer = " + a.terpopuler(3));

        for (String m : new String[]{"algoritma", "pbo", "pemrograman_lanjut"}) a.tambahKodeMatkul(m);
        a.daftarkanPengguna(new Pengguna("m001", "Dina", "mahasiswa", Set.of("pbo")));
        a.daftarkanPengguna(new Pengguna("m002", "Eko", "mahasiswa", Set.of()));
        a.daftarkanPengguna(new Pengguna("k001", "Raka", "kontributor", Set.of()));
        a.daftarkanPengguna(new Pengguna("a001", "Bu Sari", "admin", Set.of()));

        a.unggah("k001", new Materi("Modul PBO", "pbo", 2025, 4.0, Set.of("pbo"), "modul", 3));        // d01
        a.unggah("k001", new Materi("PPT PBO", "pbo", 2024, 2.5, Set.of("pbo"), "ppt", 1));            // d02
        a.unggah("k001", new Soal("Soal UAS Algoritma", "algoritma", 2025, 1.5, Set.of(), "UAS"));     // d03
        a.unggah("k001", new Materi("Modul Algoritma", "algoritma", 2024, 3.0, Set.of(), "modul", 2)); // d04
        a.unggah("k001", new ReferensiTA("Sistem Presensi QR", "pemrograman_lanjut", 2023, 6.0,
                Set.of(), "Dr. Budi", ""));                                                            // d05 (menunggu)

        for (String id : new String[]{"d01", "d02", "d03", "d04"}) a.moderasi("a001", id, true);

        System.out.println("sebelum unduh: kategori = " + a.jumlahPerKategori() + ", populer = " + a.terpopuler(3));

        a.unduh("m001", "d01");
        a.unduh("m002", "d01");
        a.unduh("m001", "d01");
        a.unduh("m001", "d04");
        a.unduh("m002", "d02");

        System.out.println("kategori = " + a.jumlahPerKategori());
        List<Dokumen> top = a.terpopuler(3);
        for (int i = 0; i < top.size(); i++) {
            Dokumen d = top.get(i);
            System.out.println((i + 1) + ". " + d.getId() + " " + d.getJudul() + " (" + d.getJumlahUnduh() + " unduhan)");
        }
        System.out.println("terpopuler(1) = " + a.terpopuler(1).get(0).getId());
    }
}