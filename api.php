<?php

use Illuminate\Http\Request;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

/*Route::middleware('auth:api')->get('/user', function (Request $request) {
    return $request->user();
});*/

Route::resource('categoria', 'CategoriaController', ['except' => ['create', 'edit']]);
Route::resource('mesa', 'MesaController', ['except' => ['create', 'edit']]);
Route::resource('empleado', 'EmpleadoController', ['except' => ['create', 'edit']]);
Route::resource('producto', 'ProductoController', ['except' => ['create', 'edit']]);
Route::resource('factura', 'FacturaController', ['except' => ['create', 'edit']]);
Route::resource('comanda', 'ComandaController', ['except' => ['create', 'edit']]);
//Route::get('empleado/login/{username}', 'EmpleadoController@login');
Route::post('upload', 'MesaController@upload');