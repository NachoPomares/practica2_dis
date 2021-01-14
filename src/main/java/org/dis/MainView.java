package org.dis;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.springframework.util.StringUtils;
import java.util.List;

@CssImport("../src/main/style.css")
@Route
public class MainView extends VerticalLayout {
    private Grid<Pelicula> grid = new Grid<>(Pelicula.class);
    private final PeliculaRepository repo;
    private final ActorRepository actorRepo;
    private final PeliculaEditor editor;

    private TextField filterText = new TextField();
    private Dialog detalles =  new Dialog();
    private VerticalLayout detallesLayout = new VerticalLayout();
    private Button editarButton= new Button("Editar película");

    private HorizontalLayout detallesTitulo = new HorizontalLayout();
    private Paragraph tituloValue = new Paragraph("");
    private HorizontalLayout detallesSinopsis = new HorizontalLayout();
    private Paragraph sinopsisValue = new Paragraph("");
    private HorizontalLayout detallesGenero = new HorizontalLayout();
    private Paragraph generoValue = new Paragraph("");
    private HorizontalLayout detallesEnlace= new HorizontalLayout();
    private Paragraph enlaceValue = new Paragraph("");
    private HorizontalLayout detallesAgno = new HorizontalLayout();
    private Paragraph agnoValue = new Paragraph("");
    private HorizontalLayout detallesDuracion = new HorizontalLayout();
    private Paragraph duracionValue = new Paragraph("");
    private HorizontalLayout detallesActions = new HorizontalLayout();
    Dialog verReparto= new Dialog();
    Button verRepartoButton = new Button("Ver reparto",buttonClickEvent -> {verReparto.open();});
    VerticalLayout verRepartoLayout = new VerticalLayout();


    public MainView(PeliculaRepository repo, ActorRepository actorRepo, PeliculaEditor editor) {
        this.repo = repo;
        this.actorRepo = actorRepo;
        this.editor = editor;
        addClassName("list-view");
        setSizeFull();
        configureFilter();
        configureGrid();
        configureDetalles();

        add(filterText,grid,detalles,editor);
        updateList(filterText);
        grid.asSingleSelect().addValueChangeListener(e -> {
            if(e.getValue() != null)
            {
                Pelicula p = e.getValue();
                tituloValue.setText(p.getTitulo());
                sinopsisValue.setText(p.getSinopsis());
                generoValue.setText(p.getGenero());
                enlaceValue.setText(p.getEnlace());
                agnoValue.setText(Integer.toString(p.getAgno()));
                duracionValue.setText(Integer.toString(p.getDuracion()));
                List<Actor> reparto = p.getReparto();
                configureVerActorLayout(reparto);
                editarButton.addClickListener(ev -> {
                    detalles.close();
                    editor.editPelicula(p);
                });
                detalles.open();
            }
        });

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            updateList(filterText);
        });
    }


    private void configureGrid() {
        grid.addClassName("pelicula-grid");
        grid.setSizeFull();
        grid.setColumns( "peliculaId","titulo", "enlace", "agno", "duracion");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

    }

    private void updateList(TextField filterText) {
        if (StringUtils.isEmpty(filterText.getValue())) {
            grid.setItems(repo.findAll());
        }
        else {
            grid.setItems(repo.findByTituloStartsWithIgnoreCase(filterText.getValue()));
        }
    }

    private void configureFilter() {
        filterText.setPlaceholder("Filtrar por título...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList(filterText));
    }

    private void configureDetalles( ){

        detalles.setCloseOnOutsideClick(false);
        detalles.setCloseOnEsc(false);
        Paragraph tituloParagraph = new Paragraph("Titulo: ");
        tituloParagraph.setClassName("componente");
        detallesTitulo.add(tituloParagraph);
        detallesTitulo.add(tituloValue);
        Paragraph sinopsisParagraph = new Paragraph("Sinopsis: ");
        sinopsisParagraph.setClassName("componente");
        detallesSinopsis.add(sinopsisParagraph);
        detallesSinopsis.add(sinopsisValue);
        Paragraph generoParagraph = new Paragraph("Genero: ");
        generoParagraph.setClassName("componente");
        detallesGenero.add(generoParagraph);
        detallesGenero.add(generoValue);
        Paragraph enlaceParagraph = new Paragraph("Enlace: ");
        enlaceParagraph.setClassName("componente");
        detallesEnlace.add(enlaceParagraph);
        detallesEnlace.add(enlaceValue);
        Paragraph agnoParagraph = new Paragraph("Agno: ");
        agnoParagraph.setClassName("componente");
        detallesAgno.add(agnoParagraph);
        detallesAgno.add(agnoValue);
        Paragraph duracionParagraph = new Paragraph("Duracion: ");
        duracionParagraph.setClassName("componente");
        detallesDuracion.add(duracionParagraph);
        detallesDuracion.add(duracionValue);

        detallesLayout.add(detallesTitulo,detallesSinopsis,detallesGenero,detallesEnlace,detallesAgno,detallesDuracion,verRepartoButton);

        detalles.add(detallesLayout);
        detallesActions.add(new Button("Cerrar", event -> {
            detalles.close();
            //grid; borrar el focus??
        }));
        detallesActions.add(editarButton);
        detalles.add(detallesActions);
    }

    public void configureVerActorLayout(List<Actor> reparto){
        verRepartoLayout.removeAll();
        for(int i = 0; i<reparto.size(); i++) {
            H3 head = new H3("Actor " + (i + 1));
            Actor a = reparto.get(i);
            Paragraph nombre = new Paragraph("Nombre: " + a.getNombre());
            Paragraph enlace = new Paragraph("Enlace a la wikipedia: " + a.getEnlaceWikipedia());
            verRepartoLayout.add(head, nombre, enlace);

        }
        verReparto.removeAll();
        verReparto.add(verRepartoLayout);
    }


}