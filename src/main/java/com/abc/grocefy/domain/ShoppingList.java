package com.abc.grocefy.domain;


import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;
import com.abc.grocefy.domain.enumeration.State;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A ShoppingList.
 */
@Entity
@Table(name = "shopping_list")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "shoppinglist")
public class ShoppingList implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "title")
    private String title;

    
    @Lob
    @Column(name = "jhi_list", nullable = false)
    private String list;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private State state;

    @ManyToOne
    @JsonIgnoreProperties("shoppingLists")
    private User owner;

    @ManyToOne
    @JsonIgnoreProperties("shoppingLists")
    private User shopper;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public ShoppingList title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getList() {
        return list;
    }

    public ShoppingList list(String list) {
        this.list = list;
        return this;
    }

    public void setList(String list) {
        this.list = list;
    }

    public State getState() {
        return state;
    }

    public ShoppingList state(State state) {
        this.state = state;
        return this;
    }

    public void setState(State state) {
        this.state = state;
    }

    public User getOwner() {
        return owner;
    }

    public ShoppingList owner(User user) {
        this.owner = user;
        return this;
    }

    public void setOwner(User user) {
        this.owner = user;
    }

    public User getShopper() {
        return shopper;
    }

    public ShoppingList shopper(User user) {
        this.shopper = user;
        return this;
    }

    public void setShopper(User user) {
        this.shopper = user;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ShoppingList shoppingList = (ShoppingList) o;
        if (shoppingList.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), shoppingList.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ShoppingList{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", list='" + getList() + "'" +
            ", state='" + getState() + "'" +
            "}";
    }
}
