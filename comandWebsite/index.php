<?php 
    ini_set('display_errors',1);
    error_reporting('E_ALL');

	define('DB_HOST','localhost');
	define('DB_PASS','comandas');
	define('DB_USER','comandas');
	define('DB_NAME','comandasDB');
	define('TPP',5);

    require_once('header.php');
	require_once('DBConnection.php');
	
    $pagina = $_GET['pagina'];
    $listaPrevia = explode(" ",$_GET['lista']);
    
	function listWords($campo){
		$quitar = array(" ", "&", "-", ".", ",", ":", ";", "|", "@", "'","`");
		$listaResult = str_replace($quitar," ", $campo);
		$_GET['lista'] = $listaResult;
		$result = explode(" ", $listaResult);
		return array_filter($result);
	}

	function formarConsulta($lista){;
		$listado = explode(" ",implode(" ",$lista));
		$sql = "SELECT comanda.id, horaInicio, unidades, nombre, numeroMesa FROM comanda 
        INNER JOIN producto ON comanda.idProducto = producto.id 
        INNER JOIN factura ON comanda.idFactura = factura.id
        WHERE comanda.entregado = 0 AND ";
        $localizacion="";
        if(isset($_GET['destino'])){
            $destino = $_GET['destino'];
        }
        if(isset($_POST['destino'])){
            $destino = $_POST['destino'];
        }
        if(!isset($destino)){
            $destino = 1;
        }
        if($destino == 1){
            $localizacion = " destino = 'cocina' ";
        }
        if($destino == 2){ 
            $localizacion = " destino = 'barra' ";
        }
		if(sizeof($listado) <= 1){
			$sql .= 'nombre like "%'.$listado[0].'%" AND '.$localizacion.' OR ';
			$sql .= 'numeroMesa ="'.$listado[0].'" AND '.$localizacion;
		} else {
			for($i = 0; $i < sizeof($listado); $i++){
                $sql .= 'nombre like "%'.$listado[$i].'%" AND'.$localizacion.' OR ';
				$sql .= 'numeroMesa ="'.$listado[$i].'" AND '.$localizacion.' ';
				if($i < sizeof($listado)-1){
					$sql .= " OR ";
				}
			}
        }
        $sql .=" ORDER BY horaInicio ASC ";
        
        return $sql;
	}

?>
<div class="wrap">
    <form action="<?php echo $_SERVER['PHP_SELF']?>" method="post" class="search">
        <input type="text" name="search"class="searchTerm" placeholder="¿Qué desea buscar?">
        <select name="destino" class="searchDestination">
            <?php 
                if(isset($_POST['destino'])){
                    $destino = $_POST['destino'];
                    if($_POST['destino'] == 1){
                        echo '<option selected value="1"> Cocina </option>';
                        echo '<option value="2">Barra</option>';
                    }
                    if($_POST['destino'] == 2){
                        echo '<option value="1"> Cocina </option>';
                        echo '<option selected value="2">Barra</option>';
                    }
                }
                if(isset($_GET['destino'])){
                    $destino = $_GET['destino'];
                    if($_GET['destino'] == 1){
                        echo '<option selected value="1"> Cocina </option>';
                        echo '<option value="2">Barra</option>';
                    }
                    if($_GET['destino'] == 2){
                        echo '<option value="1"> Cocina </option>';
                        echo '<option selected value="2">Barra</option>';
                    }
                }
                if(!isset($_POST['destino']) && !isset($_GET['destino'])){
                    echo '<option value="1"> Cocina </option>';
                    echo '<option value="2">Barra</option>';
                }
            ?>
        </select>

        <button type="submit" name="submit" class="searchButton">
            <i class="fa fa-search"></i>
        </button>
    </form>
</div>
<?php
    if ($destino == 1 || !isset($destino)) {
        echo "<style type=\"text/css\">
           html{
            background-image: url(\"images/cocina.jpg\");
            object-fit: cover;
            background-position: center;
            background-repeat: no-repeat;
            background-size: cover;
           }</style>";
    }
    if ($destino == 2) {
        echo "<style type=\"text/css\">
           body{
            background-image: url(\"images/barra.jpg\");
            object-fit: cover;
            background-position: center;
            background-repeat: no-repeat;
            background-size: cover;
           }</style>";
    }
?>
<div>
    <?php
    
        if(isset($_POST['search']) && $_POST['search'] != ""){
            echo '<h3 style="text-align:center;padding-top:20px;">Resultados para: '.$_POST['search'].' </h3>';
        }

        if(isset($_GET['lista']) && $_GET['lista'] != ""){
            echo '<h3 style="text-align:center;padding-top:20px;">Resultados para: '.$_GET['lista'].' </h3>';
        }
    
    ?>
   <?php 
   
            if($listaPrevia[0] == "" || !isset($_POST['search'])){
                $consulta = "SELECT comanda.id, horaInicio, unidades, nombre, numeroMesa FROM comanda 
                    INNER JOIN producto ON comanda.idProducto = producto.id 
                    INNER JOIN factura ON comanda.idFactura = factura.id
                    WHERE comanda.entregado = 0 AND ";
                    if(isset($_GET['destino'])){
                        $destino = $_GET['destino'];
                    }
                    if(isset($_POST['destino'])){
                        $destino = $_POST['destino'];
                    }
                    if(!isset($destino)){
                        $destino = 1;
                    }
                    if($destino == 1){
                        $consulta .= " destino ='cocina'"; 
                    }
                    if($destino == 2){
                        $consulta .= " destino ='barra'"; 
                    }
                    $consulta .=" ORDER BY horaInicio ASC ";
                $db = new DBConnection();
			}else{
				$consulta = formarConsulta($listaPrevia);
                $db = new DBConnection();
            }

            if(isset($_POST['search'])){
				$listaBuscar = listWords($_POST['search']);
				$consulta = formarConsulta($listaBuscar);
                $db = new DBConnection();
            }

            if($listaPrevia[0] != ""){
				$consulta = formarConsulta($listaPrevia);
                $db = new DBConnection();
            }
            
            //posible db connection aqui con la sentencia preparada y aqui se prepara y ejecuta
            $tuplas = TPP;
            $total = $db->getQuery($consulta)->rowCount();
            $paginasTotales = $total / $tuplas;
            $paginasTotales = ceil($paginasTotales);
            if($_GET['pagina'] <1){
                $_GET['pagina'] = 1;
            }
            
            if($_GET['pagina']>$paginasTotales){
                $_GET['pagina'] = $paginasTotales;
            }
           
			if($_GET['lista'] != "" && $_GET['pagina'] != ""){
                $consulta .= " limit ".($_GET['pagina'] -1) * $tuplas.",".$tuplas;
			} else {
                if($_GET['pagina'] == ""){
                    $_GET['pagina'] = 1;
                }
                $consulta .= " limit ".($_GET['pagina'] -1) * $tuplas.",".$tuplas;
                $consulta;
            }
            
			echo '
			<table>
			<tr class="tituloColumna">
				<th>Mesa</th>
				<th>Producto</th>
                <th>Unidades</th>
                <th>Hora</th>
                <th>Entregar</th>
			</tr>';
			
			foreach($db->getQuery($consulta) as $row){
				echo "<tr> <th>Nº ".$row['numeroMesa']."</th>";
				echo "<th>".$row['nombre']."</th>";
                echo "<th>x".$row['unidades']."</th>";
                echo "<th>".$row['horaInicio']."</th>";
                echo "<th><a href='delivery.php?comanda=".$row['id']."&destino=".$destino."'><img class='iconoentrega' src='images/deliver.svg'></a></th></tr>";
			}
            echo '</table>';
            
			$paginasTotales = ceil($paginasTotales);

			if($paginasTotales > 1){
				echo "<div class='paginacion'><p>".$_GET['pagina']."</p></div>";
                echo "<div class='paginacion'>";

                if($_GET['pagina'] >1){
                    $page = $_GET['pagina']-1;
                    echo "<a href='?pagina=".$page."&lista=".$_GET['lista']."&destino=".$destino."'><img class='rotatedhalf' src='images/arrow.svg'></a>";
                }

                if($_GET['pagina'] <$paginasTotales){
                    $page = $_GET['pagina']+1;
                    echo "<a href='?pagina=".$page."&lista=".$_GET['lista']."&destino=".$destino."'><img src='images/arrow.svg'></a>";
                }
				
				echo "</div>";
            }

            $noperder = $_GET['lista'];
            header("refresh:60;url=index.php?pagina=".$_GET['pagina']."&lista=".$noperder."&destino=".$destino);
		?>
</div>
		
<?php
	require('footer.php');
?>