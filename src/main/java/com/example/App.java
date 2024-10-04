package com.example;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.json.JSONObject;

@Contract(name = "MyAssetContract")
@Default
public class App extends ChaincodeBase implements ContractInterface {

    public static void main(String[] args) {
        new App().start(args);
    }

    @Override
    public Response init(ChaincodeStub stub) {
        System.out.println("Chaincode instantiated or upgraded");
        return newSuccessResponse("Chaincode instantiated or upgraded successfully");
    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        String function = stub.getFunction();
        switch (function) {
            case "initLedger":
                return newSuccessResponse(initLedger(stub));
            case "createAsset":
                return newSuccessResponse(createAsset(stub, stub.getStringArgs().get(0), stub.getStringArgs().get(1), Integer.parseInt(stub.getStringArgs().get(2))));
            case "queryAsset":
                return newSuccessResponse(queryAsset(stub, stub.getStringArgs().get(0)));
            case "updateAsset":
                return newSuccessResponse(updateAsset(stub, stub.getStringArgs().get(0), stub.getStringArgs().get(1), Integer.parseInt(stub.getStringArgs().get(2))));
            case "deleteAsset":
                return newSuccessResponse(deleteAsset(stub, stub.getStringArgs().get(0)));
            default:
                return newErrorResponse("Invalid function: " + function);
        }
    }

    // Add the business logic methods here
    @Transaction
    public String initLedger(ChaincodeStub stub) {
        System.out.println("Ledger initialized.");
        return "Ledger initialized";
    }

    @Transaction
    public String createAsset(ChaincodeStub stub, String assetId, String owner, int value) {
        // Check if asset already exists
        String asset = stub.getStringState(assetId);
        if (asset != null && !asset.isEmpty()) {
            return "Asset already exists";
        }

        // Create a JSON object for the asset
        JSONObject assetJSON = new JSONObject();
        assetJSON.put("owner", owner);
        assetJSON.put("value", value);

        // Store asset in the ledger
        stub.putStringState(assetId, assetJSON.toString());

        return "Asset created with ID: " + assetId;
    }

    @Transaction
    public String queryAsset(ChaincodeStub stub, String assetId) {
        // Get asset from the ledger
        String assetJSON = stub.getStringState(assetId);
        if (assetJSON == null || assetJSON.isEmpty()) {
            return "Asset not found";
        }

        return assetJSON;
    }

    @Transaction
    public String updateAsset(ChaincodeStub stub, String assetId, String newOwner, int newValue) {
        // Retrieve asset
        String assetJSON = stub.getStringState(assetId);
        if (assetJSON == null || assetJSON.isEmpty()) {
            return "Asset not found";
        }

        // Update asset details
        JSONObject updatedAsset = new JSONObject(assetJSON);
        updatedAsset.put("owner", newOwner);
        updatedAsset.put("value", newValue);

        // Store updated asset back to the ledger
        stub.putStringState(assetId, updatedAsset.toString());

        return "Asset updated with ID: " + assetId;
    }

    @Transaction
    public String deleteAsset(ChaincodeStub stub, String assetId) {
        // Check if asset exists
        String asset = stub.getStringState(assetId);
        if (asset == null || asset.isEmpty()) {
            return "Asset not found";
        }

        // Delete asset from the ledger
        stub.delState(assetId);

        return "Asset deleted with ID: " + assetId;
    }
}
