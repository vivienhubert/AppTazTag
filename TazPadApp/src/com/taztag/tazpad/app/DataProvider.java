package com.taztag.tazpad.app;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class DataProvider extends ContentProvider {

	public DataProvider() {
		// TODO Auto-generated constructor stub
	}

	/**
	 *  Cette méthode permet de supprimer une données du Content Provider.
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Retourne le type MIME des données contenues dans le Content Provider.
	 */
	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Cette méthode est utilisé pour rajouter des données à notre ContentProvider.
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Appeler afin d’initialiser le Content Provider
	 */
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Cette méthode retourne un objet Cursor sur lequel vous pouvez itérer pour récupérer les données.
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Cette méthode est utilisé pour mettre à jour une données déjà existante dans notre Content Provider.
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
