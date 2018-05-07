package items;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.util.ArrayList;

import javax.sound.midi.MidiDevice.Info;

import constants.Constants;
import ia.IAGenerator;
import juegoEspacio.Main;
import proposiciones.ItemId;
import utils.dataUtils.Probabilidad;

public class ItemsHandler {
	ArrayList<Item> items = new ArrayList<>();
	ArrayList<Item> itemsToRemove = new ArrayList<>();
	Item pendingToTake;

	public ItemsHandler() {

	}

	public void addNewItem(float x, float y) {
		float[] probabilidades = new float[InfoStore.items.size()];
		for (int i = 0; i < InfoStore.items.size(); i++) {
			probabilidades[i] = InfoStore.items.get(i).getProbability();
		}
		int id = Probabilidad.getIdByProbability(probabilidades);
		if (Constants.DEBUGFiles) {
			System.out.println(Constants.imagesPath + InfoStore.items.get(id).getPath());
		}
		ItemId i = InfoStore.items.get(id);
		items.add(new Item(x, y, i.getWidthImage(), i.getHeightImage(), i.getId(), i.getPath(),i.getInitialQuantity()));

	}

	public void restoreItem(Item i) {
		items.add(i);

	}

	public void leavedItem() {
		pendingToTake = null;
		Main.getVentana().getPanelActual().getItemStore().putMessageToUse = false;
	}
	public void increaseMaterial(ItemId ii,Item pending) {
		Main.getVentana().getPanelActual().getBuildPanel().getStuffPanel().addQuantity(ii.getId(), ii.getIncrementMaterial());
		Main.getVentana().getPanelActual().getItemsHandler().getItemsToRemove().add(pending);
	}
	public void renderItems(Graphics g) {

		for (Item i : items) {
			if (Constants.DEBUG) {
				g.setColor(Color.red);
				g.drawRect((int) (i.getX() - IAGenerator.mainPlayer.getX()),
						(int) (i.getY() - IAGenerator.mainPlayer.getY()), 10, 10);
			}
			i.renderImage(g, -IAGenerator.mainPlayer.getX(), -IAGenerator.mainPlayer.getY());
		}
		carryOutItemFunctions();

	}
	
	public void takeItem() {
		if(pendingToTake!=null) {
			ItemId ii =InfoStore.getItems().get(pendingToTake.getId());
			
		if(ii.isMaterial()) {
			increaseMaterial(ii,pendingToTake);
		}else {
		Main.getVentana().getPanelActual().getItemStore().addItem(pendingToTake);
		}
		pendingToTake=null;
		}	
	}

	public void carryOutItemFunctions() {
		boolean c = false;
		boolean result;

		for (Item i : getItems()) {
			if (i.isPlayerInside(IAGenerator.mainPlayer) && !i.isInStore()) {
				c = true;

				pendingToTake = i;
				
			}
		}
		
		
		if (c) {
			ItemId ii =InfoStore.getItems().get(pendingToTake.getId());
			if(ii.isMaterial()) {
				increaseMaterial(ii,pendingToTake);
			}
			Main.getVentana().getPanelActual().getItemStore().putMessageToGet(true,
					(int) pendingToTake.getX(IAGenerator.mainPlayer), (int) pendingToTake.getY(IAGenerator.mainPlayer));
		} else {
			pendingToTake = null;
			Main.getVentana().getPanelActual().getItemStore().putMessageToGet(false, 0, 0);
		}
		for (Item i : itemsToRemove) {
			getItems().remove(i);
		}
		itemsToRemove.removeAll(itemsToRemove);
	}

	public void resetItems() {
		items.removeAll(items);
	}

	public ArrayList<Item> getItems() {
		return items;
	}

	public Item getPendingToTake() {
		return pendingToTake;
	}

	public void setPendingToTake(Item pendingToTake) {
		this.pendingToTake = pendingToTake;
	}

	public ArrayList<Item> getItemsToRemove() {
		return itemsToRemove;
	}

}
