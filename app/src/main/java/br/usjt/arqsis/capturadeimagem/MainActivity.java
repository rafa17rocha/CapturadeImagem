package br.usjt.arqsis.capturadeimagem;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.mukesh.image_processing.ImageProcessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ImageInputHelper.ImageActionListener
{
	public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
	private ImageInputHelper imageInputHelper;
	private ImageView imageView;
	private ImageProcessor imageProcessor;

	private Bitmap bmpAtual;

	ArrayList<DataModel> dataModels;
	ListView listView;
	private CustomAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		checkAndRequestPermissions();

		imageInputHelper = new ImageInputHelper(this);
		imageInputHelper.setImageActionListener(this);

		imageView = (ImageView) findViewById(R.id.imagem);

		imageProcessor = new ImageProcessor();


		listView = (ListView) findViewById(R.id.listView);

		dataModels = new ArrayList<>();

		dataModels.add(new DataModel("Invert", false, false));
		dataModels.add(new DataModel("GreyScale", false, false));
		dataModels.add(new DataModel("GaussianBlur", false, false));
		dataModels.add(new DataModel("Shadow", false, false));
		dataModels.add(new DataModel("MeanRemoval", false, false));
		dataModels.add(new DataModel("Emboss", false, false));
		dataModels.add(new DataModel("Engrave", false, false));
		dataModels.add(new DataModel("FleaEffect", false, false));
		dataModels.add(new DataModel("BlackFilter", false, false));
		dataModels.add(new DataModel("SnowEffect", false, false));

		adapter = new CustomAdapter(dataModels, getApplicationContext());

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{

				DataModel dataModel = dataModels.get(position);

				if (!dataModel.checked && bmpAtual != null)
				{
					dataModel.checked = true;
					dataModel.enabled = false;
					bmpAtual = aplicarEfeito(bmpAtual, position + 1);
					adapter.notifyDataSetChanged();
					imageView.setImageBitmap(bmpAtual);
				}
			}
		});
	}

	private Bitmap aplicarEfeito(Bitmap imagem, int id)
	{
		switch (id)
		{
			case 1:
				return imageProcessor.doInvert(imagem);
			case 2:
				return imageProcessor.doGreyScale(imagem);
			case 3:
				return imageProcessor.applyGaussianBlur(imagem);
			case 4:
				return imageProcessor.createShadow(imagem);
			case 5:
				return imageProcessor.applyMeanRemoval(imagem);
			case 6:
				return imageProcessor.emboss(imagem);
			case 7:
				return imageProcessor.engrave(imagem);
			case 8:
				return imageProcessor.applyFleaEffect(imagem);
			case 9:
				return imageProcessor.applyBlackFilter(imagem);
			case 10:
				return imageProcessor.applySnowEffect(imagem);
		}
		return imagem;
	}

	private boolean checkAndRequestPermissions()
	{
		int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
		int storage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		int loc = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
		int loc2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
		int network = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);
		int internet = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);

		List<String> listPermissionNeeded = new ArrayList<>();

		if (camera != PackageManager.PERMISSION_GRANTED)
		{
			listPermissionNeeded.add(Manifest.permission.CAMERA);
		}
		if (storage != PackageManager.PERMISSION_GRANTED)
		{
			listPermissionNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		}
		if (loc != PackageManager.PERMISSION_GRANTED)
		{
			listPermissionNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
		}
		if (loc2 != PackageManager.PERMISSION_GRANTED)
		{
			listPermissionNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
		}
		if (network != PackageManager.PERMISSION_GRANTED)
		{
			listPermissionNeeded.add(Manifest.permission.ACCESS_NETWORK_STATE);
		}
		if (internet != PackageManager.PERMISSION_GRANTED)
		{
			listPermissionNeeded.add(Manifest.permission.INTERNET);
		}
		if (!listPermissionNeeded.isEmpty())
		{
			ActivityCompat.requestPermissions(this, listPermissionNeeded.toArray(new String[listPermissionNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
			return false;
		}
		return true;
	}

	public void capturaDaGaleria(View view)
	{
		imageInputHelper.selectImageFromGallery();
	}

	public void capturaDaCamera(View view)
	{
		imageInputHelper.takePhotoWithCamera();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		imageInputHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onImageSelectedFromGallery(Uri uri, File imageFile)
	{
		imageInputHelper.requestCropImage(uri, 400, 300, 4, 3);
	}

	@Override
	public void onImageTakenFromCamera(Uri uri, File imageFile)
	{
		imageInputHelper.requestCropImage(uri, 400, 300, 4, 3);
	}

	@Override
	public void onImageCropped(Uri uri, File imageFile)
	{
		try
		{
			bmpAtual = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
			imageView.setImageBitmap(bmpAtual);

			if (bmpAtual != null)
			{
				for (DataModel dm : dataModels)
				{
					dm.enabled = true;
					dm.checked = false;
				}

				adapter.notifyDataSetChanged();
			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
