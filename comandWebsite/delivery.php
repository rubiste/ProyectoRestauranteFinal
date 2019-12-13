<?php 
	ini_set('display_errors',1);
	error_reporting(E_ALL);

	define('DB_HOST','localhost');
	define('DB_PASS','comandas');
	define('DB_USER','comandas');
	define('DB_NAME','comandasDB');

	require_once('header.php');
    require_once('DBConnection.php');
    
    $id = $_GET['comanda'];

    if(isset($_POST['submit'])){
        if(isset($_POST['destino'])){
            $destino = $_POST['destino'];
        }else{
            $destino = 1;
        }
        if($_POST['result'] == "1"){
            $dbh = new DBConnection();
            $con = $dbh->getDBH();
            $sentencia = "UPDATE comanda set entregado=1 WHERE id = :id;";
            $stmt = $con->prepare($sentencia);
            $stmt->bindValue(':id',$id);
            $stmt->execute();
            header('Location: index.php?destino='.$destino);
            //echo "<script> window.history.go(-2); </script>";
        }
        if($_POST['result'] == "0"){
            header('Location: index.php?destino='.$destino);
            //echo "<script> window.history.go(-2); </script>";
        }
        
    }
?>
<div>
    <form class="formulario" action="<?php echo $_SERVER['PHP_SELF']."?comanda=".$_GET['comanda']."";?>" method="post">
        <h2>¿Realizar entrega de la comanda?</h2>
        <?php 
            if(isset($_GET['destino'])){
                echo "<input type='hidden' style='visibility:hidden' name='destino' value='".$_GET['destino']."'";
            }
        ?>
        <div class="optionsradio">
            <div class="radios">
                <div><input class="option-input radio" type="radio" name="result" value="1"> Sí</div>
                <div><input class="option-input radio" type="radio" name="result" value="0" checked="true"> No</div>				
            </div>
            <input type="submit" class="entregar" value="Seguir" id="delete" name="submit"/>
        </div>
    </form>
</div>
<?php
	require('footer.php');
?>