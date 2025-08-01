package school.hei.patrimoine.modele.possession;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import school.hei.patrimoine.modele.Argent;
import school.hei.patrimoine.modele.Devise;
import school.hei.patrimoine.modele.objectif.Objectivable;
import school.hei.patrimoine.modele.vente.InformationDeVente;
import school.hei.patrimoine.modele.vente.ValeurMarchee;
import school.hei.patrimoine.modele.vente.Vendable;

@ToString
@EqualsAndHashCode(callSuper = false)
public abstract sealed class Possession extends Objectivable
    implements Serializable /*note(no-serializable)*/, Vendable
    permits AchatMaterielAuComptant,
        Compte,
        CompteCorrection,
        Correction,
        FluxArgent,
        GroupePossession,
        Materiel,
        PatrimoinePersonnel,
        PersonneMorale,
        RemboursementDette,
        TransfertArgent,
        Vente {
  protected final String nom;
  protected final LocalDate t;
  protected final Argent valeurComptable;
  protected final InformationDeVente informationDeVente;
  @EqualsAndHashCode.Exclude @ToString.Exclude private CompteCorrection compteCorrection;

  public Possession(String nom, LocalDate t, Argent valeurComptable) {
    super();
    this.nom = nom;
    this.t = t;
    this.valeurComptable = valeurComptable;
    this.informationDeVente = new InformationDeVente();
  }

  public CompteCorrection getCompteCorrection() {
    if (compteCorrection == null) {
      compteCorrection = new CompteCorrection(nom, valeurComptable.devise());
    }
    return compteCorrection;
  }

  public Argent valeurComptable() {
    return valeurComptable;
  }

  public final Devise devise() {
    return valeurComptable.devise();
  }

  public final Argent valeurComptableFuture(LocalDate tFutur) {
    if (getDateDeVente() != null) {
      if (!tFutur.isBefore(getDateDeVente())) {
        return valeurComptable.mult(0);
      }
    }

    return projectionFuture(tFutur).valeurComptable();
  }

  public abstract Possession projectionFuture(LocalDate tFutur);

  public abstract TypeAgregat typeAgregat();

  @Override
  public String nom() {
    return nom;
  }

  @Override
  public Argent valeurAObjectifT(LocalDate t) {
    return projectionFuture(t).valeurComptable;
  }

  @Override
  public Set<ValeurMarchee> getValeurMarches() {
    return this.informationDeVente.getValeurMarches();
  }

  @Override
  public ValeurMarchee getValeurMarche(LocalDate t) {
    if (typeAgregat() == TypeAgregat.IMMOBILISATION || typeAgregat() == TypeAgregat.ENTREPRISE) {
      return informationDeVente.getValeurMarche(t);
    }
    return new ValeurMarchee(t, valeurComptable());
  }

  @Override
  public void addValeurMarche(ValeurMarchee v) {
    if ((typeAgregat() != TypeAgregat.IMMOBILISATION) && (typeAgregat() != TypeAgregat.ENTREPRISE))
      throw new UnsupportedOperationException(
          "Impossible d'ajouter une valeur de marché pour ce type d'agrégat");

    informationDeVente.addValeurMarche(v);
  }

  @Override
  public Argent getValeurDeVente() {
    return informationDeVente.getValeurDeVente();
  }

  @Override
  public LocalDate getDateDeVente() {
    return informationDeVente.getDateDeVente();
  }

  @Override
  public Compte getCompteBeneficiaire() {
    return informationDeVente.getCompteBeneficiaire();
  }

  @Override
  public boolean estVendue() {
    return informationDeVente.estVendue();
  }

  @Override
  public void vendre(Argent valeurDeVente, LocalDate dateDeVente, Compte compteBeneficiaire) {
    if ((typeAgregat() != TypeAgregat.IMMOBILISATION) && (typeAgregat() != TypeAgregat.ENTREPRISE))
      throw new UnsupportedOperationException("Impossible de vendre ce type d'agrégat");
    informationDeVente.confirmeVente(this, valeurDeVente, dateDeVente, compteBeneficiaire);
  }
}
