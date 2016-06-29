
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.applet.Applet;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Material;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.BoxLayout;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3f;


public class Robot_Cylindryczny extends Applet implements ActionListener, KeyListener{
 

    private TransformGroup trans_podstawa, trans_kolumna, trans_ramie1, trans_ramie2,trans_kisc ,trans_chwyt_1 ,
                        trans_chwyt_2 , trans_pilka, objRotate;

    private Transform3D przesuniecie_obserwatora = new Transform3D();
    private Transform3D obrot1 = new Transform3D();
    private Transform3D przes1 = new Transform3D();
    private Transform3D przes2 = new Transform3D();
    private Transform3D poz_pilka = new Transform3D();
    
    private SimpleUniverse u; 
  
    private float angle;
    private float h = 0.25f;
    private float r = 0.35f;
    private float krok = 0.05f;
    private float buf = 0.00f;
    private float predkosc;  
    
    private int tryb;
    private int next;
    private int aktualny_ruch;

    private boolean trzyma_pilke=false;
    
    private Button reset = new Button("Ustawienie poczatkowe");
    private Button nauka = new Button("Nauka");
    private Button naukaSek = new Button("Nauka sekwencji");
    private Button naukaOff = new Button("Zakoncz nauke");
    
    xyz[] coords = new xyz[100];
    Vector3f wspolrzedne_pilki = new Vector3f();
    
    class xyz{              //klasa wspolrzednych
       private float x;
       private float y;
       private float z;
 
      public xyz(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
      }
      private xyz() {
            this.x = 0;
            this.y = 0;
            this.z = 0;
      }
      
      public float getX() { return x; }
      public float getY() { return y; }
      public float getZ() { return z; }
     
      public void setX(float x) { this.x = x; }
      public void setY(float y) { this.y = y; }
      public void setZ(float z) { this.z = z; }
    }

    public void ustaw(xyz xyz){
        r = xyz.getX() ;
        angle = xyz.getY();
        h = xyz.getZ();
            
            //Pozycja startowa:
            ObrotKolumnaLewo(krok);
            PrzesRamieGora(krok);
            PrzesChwytakPrzod(krok);
    }
    
//Odtwarzanie nagrania
    public void play(xyz wsp1, xyz wsp2)
    {   
        //Różnica pozycji docelowej i obecnej
        buf = wsp2.getY() - wsp1.getY();
        
        if (Math.abs(buf) > 0.1f){
        ObrotKolumnaLewo(krok*predkosc*Math.signum(buf));
        }
        
        buf = wsp2.getZ() - wsp1.getZ();
        
        if (Math.abs(buf) > 0.05f){
        PrzesRamieGora(krok*predkosc*Math.signum(buf));
        }
        
        buf = wsp2.getX() - wsp1.getX();
        
        if (Math.abs(buf) > 0.05f){
        PrzesChwytakPrzod(krok*predkosc*Math.signum(buf));
        }
        
        
        else{
            buf = wsp2.getZ() - wsp1.getZ();
            if (Math.abs(buf) < 0.05f){
            
                    buf = wsp2.getY() - wsp1.getY();
                    if (Math.abs(buf) < 0.1f){
                        aktualny_ruch +=1;
                        predkosc = 1;
                       
                    }
                }
            }     
    }
    
    //Metody odpowiadajace za ruch:
    
    public void PrzesRamieGora(float krok){       
            h += krok;
            if(h > 0.25f)h = 0.25f;
            przes1.setTranslation(new Vector3f(0.15f,h,0.0f));  
            trans_ramie1.setTransform(przes1);
    }
    
     public void PrzesRamieDol(float krok){
            h -= krok; 
            if(h < -0.2f) h = -0.2f;
            przes1.setTranslation(new Vector3f(0.15f,h,0.0f));  
            trans_ramie1.setTransform(przes1);
    }
    public void ObrotKolumnaLewo(float krok){
            angle += krok;
            obrot1.rotY(angle);  
            trans_kolumna.setTransform(obrot1);
    }
   
    public void ObrotKolumnaPrawo(float krok){
            angle -= krok; 
            obrot1.rotY(angle);
            trans_kolumna.setTransform(obrot1);
    }

    public void PrzesChwytakPrzod(float krok){
            r += krok; 
            if(r > 0.4f) r = 0.4f;
            przes2.rotZ(Math.PI/2);
            przes2.setTranslation(new Vector3f(r,0.0f,0.0f));  
            trans_ramie2.setTransform(przes2);
    }
    
    public void PrzesChwytakTyl(float krok){
            r -= krok; 
            if(r < 0.1f) r = 0.1f;
            przes2.rotZ(Math.PI/2);
            przes2.setTranslation(new Vector3f(r,0.0f,0.0f));  
            trans_ramie2.setTransform(przes2);    
    }
    
    public void Dostosuj_pilke(Transform3D poz_pileczka){
            wspolrzedne_pilki.y=h+0.3f;
            wspolrzedne_pilki.x=(float) (Math.sin(angle+1.57)*(r+0.4f));
            wspolrzedne_pilki.z=(float) (Math.cos(angle+1.57)*(r+0.4f)) ;
            poz_pileczka.set(wspolrzedne_pilki);
            trans_pilka.setTransform(poz_pileczka); 
    }
   
     public void ball_drop(){
         
         do{
             wspolrzedne_pilki.y -= 0.00001;
             poz_pilka.set(wspolrzedne_pilki);
             trans_pilka.setTransform(poz_pilka);
         
         }while(wspolrzedne_pilki.y > 0.06);     
     }
    
    //Konstruktor
    Robot_Cylindryczny(){
     

        coords[0] = new xyz();
        coords[1] = new xyz();
        
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Elementy okna
        
        Canvas3D canvas = new Canvas3D(config); //Płótno
        add(canvas);
        canvas.setPreferredSize(new Dimension (1024,670));
        canvas.addKeyListener(this);
       
        Panel p = new Panel();       //Stworzenie panelu z przyciskami.
        add(p);
        p.setPreferredSize(new Dimension (1024, 100));
        p.setBackground(Color.gray);
        
        // dodanie przycisków do panelu:
        reset.setPreferredSize(new Dimension(160,45)); 
        p.add(reset);
        reset.addActionListener(this);
        reset.addKeyListener(this);

        nauka.setPreferredSize(new Dimension(160,45));
        p.add(nauka); 
        nauka.addActionListener(this);
        nauka.addKeyListener(this);

        naukaSek.setPreferredSize(new Dimension(170,45));
        p.add(naukaSek); 
        naukaSek.addActionListener(this);
        naukaSek.addKeyListener(this);

        naukaOff.setPreferredSize(new Dimension(160,45)); 
        p.add(naukaOff); 
        naukaOff.addActionListener(this);
        naukaOff.addKeyListener(this);

      // Obrot kamery:
        u = new SimpleUniverse(canvas);
		OrbitBehavior platforma = new OrbitBehavior(canvas, OrbitBehavior.REVERSE_ROTATE);
        platforma.setSchedulingBounds (new BoundingSphere());
        u.getViewingPlatform().getViewPlatformTransform().setTransform(przesuniecie_obserwatora);
        u.getViewingPlatform().setViewPlatformBehavior (platforma);
       
        przesuniecie_obserwatora.set(new Vector3f(0.0f,0.2f,4.0f));
        u.getViewingPlatform().getViewPlatformTransform().setTransform(przesuniecie_obserwatora);
       
        BranchGroup scene = utworzScene(u);
			scene.compile();
        u.addBranchGraph(scene);
    
    }
     
     public BranchGroup utworzScene(SimpleUniverse su){
         
        BranchGroup Scena = new BranchGroup();
        
        Material mat = new Material();          //materiał cylindrów
        mat.setAmbientColor(new Color3f(0.54f, 0.02f, 0.02f)); 
       
        Material mat2 = new Material(); // materiał podstawy
        mat2.setAmbientColor(new Color3f(0.392f, 0.314f, 0.196f));     
        
        //tekstura ramienia 1:
        Appearance wyglad_ramie1   = new Appearance();
         TextureLoader loader = new TextureLoader("obrazki/text.jpg",null);
        ImageComponent2D image3 =  loader.getImage();

        Texture2D tek_ram_1 = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA,
                                        image3.getWidth(), image3.getHeight());
        tek_ram_1.setImage(0, image3);
        tek_ram_1.setBoundaryModeS(Texture.WRAP);
        tek_ram_1.setBoundaryModeT(Texture.WRAP);      
        
        wyglad_ramie1.setTexture(tek_ram_1);
        wyglad_ramie1.setMaterial(mat);
        
        TextureLoader laduj = new TextureLoader("obrazki/niebo.jpg",this);
        ImageComponent2D image = loader.getImage();

        Texture2D niebo = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA,
                                        image.getWidth(), image.getHeight());
        niebo.setImage(0, image);
        niebo.setBoundaryModeS(Texture.WRAP);
        niebo.setBoundaryModeT(Texture.WRAP);
        
      //tekstura podlogi:
        Appearance wyglad3   = new Appearance();
        loader = new TextureLoader("obrazki/podloga.jpg",null);
        ImageComponent2D image5 =  loader.getImage();

        Texture2D tek_wyglad3 = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA,
                                        image5.getWidth(), image5.getHeight());
        tek_wyglad3.setImage(0, image5);
        tek_wyglad3.setBoundaryModeS(Texture.WRAP);
        tek_wyglad3.setBoundaryModeT(Texture.WRAP);      
        
        wyglad3.setTexture(tek_wyglad3);
        wyglad3.setMaterial(mat);
        
        
        Appearance wyglad = new Appearance();
        wyglad.setColoringAttributes(new ColoringAttributes(10.5f,10.5f,10.5f,ColoringAttributes.NICEST));
        wyglad.setMaterial(mat);
       
        Appearance wyglad2 = new Appearance();
        wyglad2.setColoringAttributes(new ColoringAttributes(10.5f,10.5f,10.5f,ColoringAttributes.NICEST));
        wyglad2.setMaterial(mat2);
        
        //podstawka robota:
        trans_podstawa = new TransformGroup();
       
         QuadArray podloga = new QuadArray(4, GeometryArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2);
         Point3f p = new Point3f();
         p.set(-4.0f, 0.0f, -4.0f);
         podloga.setCoordinate(0, p);
         p.set(-4.0f, 0.0f, 4.0f);
         podloga.setCoordinate(1, p);
         p.set(4.0f, 0.0f, 4.0f);
         podloga.setCoordinate(2, p);
         p.set(4.0f, 0.0f, -4.0f);
         podloga.setCoordinate(3, p);

           TexCoord2f q = new TexCoord2f();
         q.set(0.0f, 1.0f);
         podloga.setTextureCoordinate(0, 0, q);
         q.set(0.0f, 0.0f);
         podloga.setTextureCoordinate(0, 1, q);
         q.set(1.0f, 0.0f);
         podloga.setTextureCoordinate(0, 2, q);
         q.set(1.0f, 1.0f);
         podloga.setTextureCoordinate(0, 3, q);
        
        Box podstawa = new Box(0.15f,0.03f,0.15f, wyglad2);
        
        trans_podstawa.addChild(new Shape3D(podloga, wyglad3));
        trans_podstawa.addChild(podstawa);
  
        //Kolumna:
        Cylinder walec = new Cylinder(0.05f, 0.6f,Cylinder.GENERATE_NORMALS| Cylinder.GENERATE_TEXTURE_COORDS, wyglad);
        Transform3D poz_walec = new Transform3D();
        poz_walec.set(new Vector3f(0.0f, 0.3f, 0));
        TransformGroup przes_walec = new TransformGroup(poz_walec);
        przes_walec.addChild(walec);
        
        trans_kolumna = new TransformGroup();
        trans_kolumna.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_kolumna.addChild(przes_walec);
        trans_podstawa.addChild(trans_kolumna);
        
        //Ramie:
        Box ramie1 = new Box(0.25f, 0.05f, 0.075f,Box.GENERATE_NORMALS| Box.GENERATE_TEXTURE_COORDS, wyglad_ramie1);
         
        Transform3D  poz_ramie1   = new Transform3D();
        poz_ramie1.set(new Vector3f(0.15f,0.25f,0.0f));
        
        trans_ramie1 = new TransformGroup(poz_ramie1);
        trans_ramie1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_ramie1.addChild(ramie1);
        
        przes_walec.addChild(trans_ramie1);
        
        //Drugie ramię:
        Cylinder ramie2 = new Cylinder(0.02f, 0.4f, wyglad);
        Transform3D  poz_ramie2 = new Transform3D();
        poz_ramie2.set(new Vector3f(0.35f,0.0f,0.0f));
        
        Transform3D tmp_rot = new Transform3D(); 
        tmp_rot.rotZ(Math.PI/2);                
        poz_ramie2.mul(tmp_rot);
        
        trans_ramie2 = new TransformGroup(poz_ramie2);
        trans_ramie2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_ramie2.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        trans_ramie2.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        trans_ramie2.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        trans_ramie2.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        trans_ramie2.addChild(ramie2);
        
        trans_ramie1.addChild(trans_ramie2);
        
        //Kiść:
        Box kisc = new Box(0.018f, 0.05f, 0.075f,Box.GENERATE_NORMALS| Box.GENERATE_TEXTURE_COORDS, wyglad_ramie1);
         
        Transform3D  poz_kisc  = new Transform3D(); 
        poz_kisc.set(new Vector3f(0.0f,-0.18f,0.0f));
        poz_kisc.mul(tmp_rot);
        
        trans_kisc = new TransformGroup(poz_kisc);
        
        trans_kisc.addChild(kisc);
        trans_ramie2.addChild(trans_kisc);
               
        Box chwyt_1 = new Box(0.048f, 0.05f, 0.006f,Box.GENERATE_NORMALS| Box.GENERATE_TEXTURE_COORDS, wyglad);
        Box chwyt_2 = new Box(0.048f, 0.05f, 0.006f,Box.GENERATE_NORMALS| Box.GENERATE_TEXTURE_COORDS, wyglad);
        
        Transform3D  poz_chwyt_1  = new Transform3D();
        poz_chwyt_1.set(new Vector3f(-0.05f,0.0f,0.068f));
        poz_chwyt_1.mul(tmp_rot);
        
        Transform3D  poz_chwyt_2  = new Transform3D();
        poz_chwyt_2.set(new Vector3f(-0.05f,0.0f,-0.068f));
        poz_chwyt_2.mul(tmp_rot);

        trans_chwyt_1 = new TransformGroup(poz_chwyt_1);
        trans_chwyt_2 = new TransformGroup(poz_chwyt_2);
        
        trans_chwyt_1.addChild(chwyt_1);
        trans_chwyt_2.addChild(chwyt_2);
        
        trans_kisc.addChild(trans_chwyt_1);
        trans_kisc.addChild(trans_chwyt_2);

        //Obiekt:
        Appearance wyglad_pilka   = new Appearance();
        wyglad_pilka.setColoringAttributes(new ColoringAttributes(0.2f,0.9f,0.2f,ColoringAttributes.NICEST));
        Sphere pilka = new Sphere(0.06f, 80, wyglad_pilka);
        poz_pilka.set(new Vector3f(0.8f,0.9f,0.0f));
       
        poz_pilka.get(wspolrzedne_pilki);
        
        trans_pilka = new TransformGroup(poz_pilka);
        trans_pilka.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_pilka.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        trans_pilka.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        trans_pilka.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        trans_pilka.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        trans_pilka.addChild(pilka);
        
        trans_podstawa.addChild(trans_pilka);
               
        objRotate = new TransformGroup();
        objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        objRotate.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        objRotate.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        objRotate.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);

        Scena.addChild(objRotate);
        objRotate.addChild(trans_podstawa); 
        
       //Światła:
        BoundingSphere bounds = new BoundingSphere ();
        AmbientLight lightA = new AmbientLight(new Color3f(1.0f,1.0f,1.0f));
        lightA.setInfluencingBounds(bounds);
        Scena.addChild(lightA);
        
        Color3f light1Color = new Color3f(0.8f,0.3f,0.3f); 
        Vector3f light1Direction = new Vector3f(5.0f,5.0f,-5.0f); 
        DirectionalLight lightD = new DirectionalLight(light1Color, light1Direction);
        lightD.setInfluencingBounds(bounds);
        Scena.addChild(lightD);   
        
        //Stworzenie nieba
        Appearance wyglad_niebo = new Appearance();
        TextureLoader loader2 = new TextureLoader("obrazki/niebo.jpg",null);
        ImageComponent2D image2 = loader2.getImage();
        Texture2D chmury = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA,
                                        image2.getWidth(), image2.getHeight());

        chmury.setImage(0, image2);
        chmury.setBoundaryModeS(Texture.WRAP);
        chmury.setBoundaryModeT(Texture.WRAP);
        wyglad_niebo.setTexture(chmury);
        
        TransformGroup xniebo = new TransformGroup();
        Transform3D przesuniecie_nieba = new Transform3D();
        przesuniecie_nieba.set(new Vector3f(0.0f,2.5f,0.0f));
        xniebo.setTransform(przesuniecie_nieba);
        
        TransformGroup kula_p = new TransformGroup();
        Transform3D przesuniecie_kuli = new Transform3D();
        przesuniecie_kuli.set(new Vector3f(0.0f,0.0f,0.0f));
        kula_p.setTransform(przesuniecie_nieba);
        Sphere kula = new Sphere(20.0f,Sphere.GENERATE_NORMALS_INWARD| Sphere.GENERATE_TEXTURE_COORDS, wyglad_niebo);
        kula_p.addChild(kula);
        podstawa.addChild(kula_p);


        ball_drop();
        return Scena;
        
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        //Przyciski:
        if(e.getSource() == reset){         
            ustaw(new xyz(0.30f, 0f, 0.25f));
            if(trzyma_pilke) trzyma_pilke=!trzyma_pilke;
            ball_drop();
        }
        
        if(e.getSource() == nauka){ 
            tryb = 1;
            coords[1].setX(coords[0].getX());
            coords[1].setY(coords[0].getY());
            coords[1].setZ(coords[0].getZ());
                        
            next = 2;
        }
        
        if(e.getSource() == naukaSek){ 
            tryb = 3;
            coords[1].setX(coords[0].getX());
            coords[1].setY(coords[0].getY());
            coords[1].setZ(coords[0].getZ());
           
            next = 2;
        }
        
        if(e.getSource() == naukaOff){ 
            ustaw(new xyz(0.30f, 0f, 0.25f));
            if(trzyma_pilke) trzyma_pilke=!trzyma_pilke;
            ball_drop();
            tryb = 2;
            aktualny_ruch = 1;
        }
            
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
        predkosc +=0.1;
         if (predkosc >= 1) predkosc = 1;
        switch(e.getKeyCode()){
                    case KeyEvent.VK_SPACE:
                        przesuniecie_obserwatora.set(new Vector3f(0.0f,0.2f,4.0f));
                        u.getViewingPlatform().getViewPlatformTransform().setTransform(przesuniecie_obserwatora);
                        break;
                        
                     case KeyEvent.VK_UP:
                        PrzesRamieGora(krok*predkosc/5);
                        if(trzyma_pilke)
                        Dostosuj_pilke(poz_pilka);
                        break;
                    
                    case KeyEvent.VK_DOWN:
                        PrzesRamieDol(krok*predkosc/5);
                        if(trzyma_pilke)
                        Dostosuj_pilke(poz_pilka);
                        break;    
                    
                    case KeyEvent.VK_LEFT:
                        ObrotKolumnaLewo(krok*predkosc);
                        if(trzyma_pilke)
                        Dostosuj_pilke(poz_pilka);
                        break;
                    
                    case KeyEvent.VK_RIGHT:
                        ObrotKolumnaPrawo(krok*predkosc);
                        if(trzyma_pilke)
                        Dostosuj_pilke(poz_pilka);
                        break;
                    
                    case KeyEvent.VK_A:
                        PrzesChwytakPrzod(krok*predkosc/5);
                        if(trzyma_pilke)
                        Dostosuj_pilke(poz_pilka);
                        break;
                    // mapa@eti.pg.gda.pl
                    case KeyEvent.VK_Z:
                        PrzesChwytakTyl(krok*predkosc/5);
                        if(trzyma_pilke)
                        Dostosuj_pilke(poz_pilka);
                        break;
                        
                    case KeyEvent.VK_C:
                        if (trzyma_pilke)
                        {
                            trzyma_pilke=!trzyma_pilke;
                            ball_drop();
                                
                        }
                        else if((h+0.3-wspolrzedne_pilki.y)<0.07 && Math.abs((Math.sin(angle+1.57)*(r+0.4f))-wspolrzedne_pilki.x)<0.05 && Math.abs((Math.cos(angle+1.57)*(r+0.4f))-wspolrzedne_pilki.z)<0.05)
                        {
                            trzyma_pilke=!trzyma_pilke;
                            Dostosuj_pilke(poz_pilka);
                        }
                        
                        break;    
                    case KeyEvent.VK_ENTER: //Wrzucić w odtwarzanie przyciskiem?
                        if(tryb == 2){
                            play(coords[0], coords[aktualny_ruch]);
                        
                        }
                       
                        break;
                    
        }               
                        
        //Aktualna pozycja
        coords[0].setX(r);
        coords[0].setY(angle);
        coords[0].setZ(h);

    }

    @Override
    public void keyReleased(KeyEvent e) {
        predkosc = 1;
        if (tryb == 1){
        coords[next] = new xyz(r,angle,h);
        }
    }

    // Main
    public static void main(String[] args) {
     
        Robot_Cylindryczny okno = new Robot_Cylindryczny();
        okno.addKeyListener(okno);
        MainFrame mf = new MainFrame(okno, 1024, 720); 
    }


}