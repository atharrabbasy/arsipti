package arsipti;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.TreeMap;
import java.util.TreeSet;

public class Arsip {

    private final Map<String, Dokumen> dokumen = new LinkedHashMap<>();
    private final Map<String, Pengguna> pengguna = new LinkedHashMap<>();
    private final Set<String> kodeMatkulValid = new LinkedHashSet<>();
    private int penghitungId = 0;

    public void tambahKodeMatkul(String kode) throws ArsipException {
        if (kode == null || kode.isBlank()) {
            throw new ArsipException("Kode mata kuliah tidak boleh kosong.");
        } 
        kodeMatkulValid.add(kode.trim().toLowerCase());
    }

    public void daftarkanPengguna(Pengguna p) throws ArsipException {
        p.validasi();
        if (pengguna.containsKey(p.getId())) {
            throw new ArsipException("ID pengguna" + p.getId() + " sudah terdaftar.");
        }
        pengguna.put(p.getId(), p);
    }

    public String unggah(String idPengguna, Dokumen d) throws ArsipException {
        String idUser = idPengguna == null ? "" : idPengguna.trim().toLowerCase();
        if (!pengguna.containsKey(idUser)) {
            throw new ArsipException("Pengguna" + idUser + "tidak terdaftar>");
        }

        d.validasi();

        if (!kodeMatkulValid.contains(d.getKodeMatkul())) {
            throw new ArsipException("Kode mata kuliah '" + d.getKodeMatkul() + "' tidak dikenal.");
        }

        for (Dokumen ada : dokumen.values()) {
            boolean sama = ada.getJudul().equalsIgnoreCase(d.getJudul())
                    && ada.getKodeMatkul().equals(d.getKodeMatkul())
                    && ada.getTahun() == d.getTahun();
            if (sama) {
                throw new ArsipException("Dokumen yang sama sudah ada (" + ada.getId() + ").");
            }
        }

        penghitungId++;
        String id = String.format("d%02d", penghitungId);
        d.tetapkanId(id);
        dokumen.put(id, d);
        return id;
    }

    public void moderasi(String idAdmin, String idDokumen, boolean setuju) throws ArsipException {
        Pengguna admin = cariPengguna(idAdmin);
        if (admin == null) {
            throw new ArsipException("Pengguna " + idAdmin + " tidak terdaftar.");
        }

        if (!admin.bolehModerasi()){
            throw new ArsipException("Hanya admin yang dapat moderasi dokumen.");
        }
        Dokumen d = cariId(idDokumen);
        if (d == null) {
            throw new ArsipException("Dokumen " + idDokumen + " tidak ditemukan.");
        }
        if (setuju) {
            d.setujui();
        } else {
            d.tolak();
        }
    }

    public List<Dokumen> cari(String kata, String kategori, int tahunMin) {
        String kat = kategori == null ? "" : kategori.trim().toLowerCase();
        return dokumen.values().stream()
                .filter(d -> d.getStatus() == Dokumen.Status.DISETUJUI)
                .filter(d -> d.cocokKataKunci(kata))
                .filter(d -> kat.isEmpty() || d.getKategori().equals(kat))
                .filter(d -> d.getTahun() >= tahunMin)
                .sorted(Comparator.comparingInt(Dokumen::getTahun).reversed()
                        .thenComparing(Dokumen::getId))
                .collect(Collectors.toList());
    }

    public void unduh(String idPengguna, String idDokumen) throws ArsipException {
        Pengguna p = cariPengguna(idPengguna);
        if (p == null) {
            throw new ArsipException("Pengguna " + idPengguna + " tidak terdaftar.");
        }
        Dokumen d = cariId(idDokumen);
        if (d == null) {
            throw new  ArsipException("Dokumen " + idDokumen + " tidak ditemukan.");
        }
        d.tambahUnduh();
        p.catatUnduh(d.getId());
    }

    public Map<String, Long> jumlahPerKategori() {
        return dokumen.values().stream()
                .filter(d -> d.getStatus() == Dokumen.Status.DISETUJUI)
                .collect(Collectors.groupingBy(Dokumen::getKategori, TreeMap::new, Collectors.counting()));
    }

    public List<Dokumen> terpopuler(int n) {
        return dokumen.values().stream()
                .filter(d -> d.getJumlahUnduh() > 0)
                .sorted(Comparator.comparingInt(Dokumen::getJumlahUnduh).reversed()
                        .thenComparing(Dokumen::getId))
                .limit(n)
                .collect(Collectors.toList());
    }

    public List<String> faktaProlog() {
        List<String>  f = new ArrayList<>();
        f.add(":- dynamic matkul/1, dokumen_matkul/2, kategori_dokumen/2, disetujui/1, sedang_ambil/2, pernah_unduh/2.");

        f.add("");
        f.add("% matkul/1");
        for (String m : kodeMatkulValid){
            f.add("matkul(" + atom(m) + ").");
        }
        f.add("");
        f.add("% dokumen_matkul/2");
        for (Dokumen d : dokumen.values()) {
            f.add("dokumen_matkul(" + atom(d.getId()) + ", " + atom(d.getKodeMatkul()) + ").");
        }
        f.add("");
        f.add("% kategori_dokumen/2");
        for (Dokumen d : dokumen.values()) {
            f.add("kategori_dokumen(" + atom(d.getId()) + ", " + atom(d.getKategori()) + ").");
        }
        f.add("");
        f.add("% disetujui/1 (hanya dokumen berstatus DISETUJUI)");
        for (Dokumen d : dokumen.values()) {
            if (d.getStatus() == Dokumen.Status.DISETUJUI) {
                f.add("disetujui(" + atom(d.getId()) + ").");
            }
        }
        f.add("");
        f.add("% sedang_ambil/2");
        for (Pengguna p : pengguna.values()) {
            for (String m : new TreeSet<>(p.getMatkulDiambil())) { 
                f.add("sedang_ambil(" + atom(p.getId()) + ", " + atom(m) +").");
            }
        }
        f.add("");
        f.add("% pernah_unduh/2");
        for (Pengguna p : pengguna.values()) {
            for (String idDok : new TreeSet<>(p.getRiwayatUnduh())) {
                f.add("pernah_unduh(" + atom(p.getId()) + ", " + atom(idDok) + ").");
            }
        }
        return f;
    }

    private static String atom(String teks) {
        String a = teks == null ? "" : teks.trim().toLowerCase().replaceAll("[^a-z0-9_]", "_");
        if (a.isEmpty() || !Character.isLetter(a.charAt(0))) {
            a = "x" + a;
        }
        return a;
    }


    public Dokumen cariId(String id) {
        return id == null ? null : dokumen.get(id.trim().toLowerCase());
    }

    public Pengguna cariPengguna(String id) {
        return id == null ? null : pengguna.get(id.trim().toLowerCase());
    }

    public int jumlahDokumen() {
        return dokumen.size();
    }

    public List<Dokumen> semuaDokumen() {
        return new ArrayList<>(dokumen.values());
    }
}