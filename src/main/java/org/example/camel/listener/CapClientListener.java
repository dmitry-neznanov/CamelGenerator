package org.example.camel.listener;

import org.restcomm.protocols.ss7.cap.api.CAPDialog;
import org.restcomm.protocols.ss7.cap.api.CAPDialogListener;
import org.restcomm.protocols.ss7.cap.api.CAPMessage;
import org.restcomm.protocols.ss7.cap.api.dialog.CAPGeneralAbortReason;
import org.restcomm.protocols.ss7.cap.api.dialog.CAPGprsReferenceNumber;
import org.restcomm.protocols.ss7.cap.api.dialog.CAPNoticeProblemDiagnostic;
import org.restcomm.protocols.ss7.cap.api.dialog.CAPUserAbortReason;
import org.restcomm.protocols.ss7.cap.api.errors.CAPErrorMessage;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.*;
import org.restcomm.protocols.ss7.tcap.asn.comp.PAbortCauseType;
import org.restcomm.protocols.ss7.tcap.asn.comp.Problem;

public class CapClientListener implements
        CAPServiceCircuitSwitchedCallListener,
        CAPDialogListener {

    @Override
    public void onDialogDelimiter(CAPDialog capDialog) {

    }

    @Override
    public void onDialogRequest(CAPDialog capDialog, CAPGprsReferenceNumber capGprsReferenceNumber) {

    }

    @Override
    public void onDialogAccept(CAPDialog capDialog, CAPGprsReferenceNumber capGprsReferenceNumber) {

    }

    @Override
    public void onDialogUserAbort(CAPDialog capDialog, CAPGeneralAbortReason capGeneralAbortReason, CAPUserAbortReason capUserAbortReason) {

    }

    @Override
    public void onDialogProviderAbort(CAPDialog capDialog, PAbortCauseType pAbortCauseType) {

    }

    @Override
    public void onDialogClose(CAPDialog capDialog) {

    }

    @Override
    public void onDialogRelease(CAPDialog dialog) {

        System.out.println(
                "CAP dialog released. Dialog ID=" +
                        dialog.getLocalDialogId()
        );
    }

    @Override
    public void onDialogTimeout(CAPDialog dialog) {

        System.err.println(
                "CAP dialog timeout. Dialog ID=" +
                        dialog.getLocalDialogId()
        );
    }

    @Override
    public void onDialogNotice(CAPDialog capDialog, CAPNoticeProblemDiagnostic capNoticeProblemDiagnostic) {

    }

    @Override
    public void onInitialDPRequest(InitialDPRequest initialDPRequest) {

    }

    @Override
    public void onRequestReportBCSMEventRequest(RequestReportBCSMEventRequest requestReportBCSMEventRequest) {

    }

    @Override
    public void onApplyChargingRequest(ApplyChargingRequest applyChargingRequest) {

    }

    @Override
    public void onEventReportBCSMRequest(EventReportBCSMRequest eventReportBCSMRequest) {

    }

    @Override
    public void onContinueRequest(ContinueRequest request) {

        CAPDialogCircuitSwitchedCall dialog =
                request.getCAPDialog();

        System.out.println(
                "CAP Continue received. Dialog ID=" +
                        dialog.getLocalDialogId()
        );
    }

    @Override
    public void onContinueWithArgumentRequest(ContinueWithArgumentRequest continueWithArgumentRequest) {

    }

    @Override
    public void onApplyChargingReportRequest(ApplyChargingReportRequest applyChargingReportRequest) {

    }

    @Override
    public void onReleaseCallRequest(ReleaseCallRequest releaseCallRequest) {

    }

    @Override
    public void onConnectRequest(ConnectRequest connectRequest) {

    }

    @Override
    public void onCallInformationRequestRequest(CallInformationRequestRequest callInformationRequestRequest) {

    }

    @Override
    public void onCallInformationReportRequest(CallInformationReportRequest callInformationReportRequest) {

    }

    @Override
    public void onActivityTestRequest(ActivityTestRequest activityTestRequest) {

    }

    @Override
    public void onActivityTestResponse(ActivityTestResponse activityTestResponse) {

    }

    @Override
    public void onAssistRequestInstructionsRequest(AssistRequestInstructionsRequest assistRequestInstructionsRequest) {

    }

    @Override
    public void onEstablishTemporaryConnectionRequest(EstablishTemporaryConnectionRequest establishTemporaryConnectionRequest) {

    }

    @Override
    public void onDisconnectForwardConnectionRequest(DisconnectForwardConnectionRequest disconnectForwardConnectionRequest) {

    }

    @Override
    public void onDisconnectLegRequest(DisconnectLegRequest disconnectLegRequest) {

    }

    @Override
    public void onDisconnectLegResponse(DisconnectLegResponse disconnectLegResponse) {

    }

    @Override
    public void onDisconnectForwardConnectionWithArgumentRequest(DisconnectForwardConnectionWithArgumentRequest disconnectForwardConnectionWithArgumentRequest) {

    }

    @Override
    public void onConnectToResourceRequest(ConnectToResourceRequest connectToResourceRequest) {

    }

    @Override
    public void onResetTimerRequest(ResetTimerRequest resetTimerRequest) {

    }

    @Override
    public void onFurnishChargingInformationRequest(FurnishChargingInformationRequest furnishChargingInformationRequest) {

    }

    @Override
    public void onSendChargingInformationRequest(SendChargingInformationRequest sendChargingInformationRequest) {

    }

    @Override
    public void onSpecializedResourceReportRequest(SpecializedResourceReportRequest specializedResourceReportRequest) {

    }

    @Override
    public void onPlayAnnouncementRequest(PlayAnnouncementRequest playAnnouncementRequest) {

    }

    @Override
    public void onPromptAndCollectUserInformationRequest(PromptAndCollectUserInformationRequest promptAndCollectUserInformationRequest) {

    }

    @Override
    public void onPromptAndCollectUserInformationResponse(PromptAndCollectUserInformationResponse promptAndCollectUserInformationResponse) {

    }

    @Override
    public void onCancelRequest(CancelRequest cancelRequest) {

    }

    @Override
    public void onInitiateCallAttemptRequest(InitiateCallAttemptRequest initiateCallAttemptRequest) {

    }

    @Override
    public void onInitiateCallAttemptResponse(InitiateCallAttemptResponse initiateCallAttemptResponse) {

    }

    @Override
    public void onMoveLegRequest(MoveLegRequest moveLegRequest) {

    }

    @Override
    public void onMoveLegResponse(MoveLegResponse moveLegResponse) {

    }

    @Override
    public void onCollectInformationRequest(CollectInformationRequest collectInformationRequest) {

    }

    @Override
    public void onSplitLegRequest(SplitLegRequest splitLegRequest) {

    }

    @Override
    public void onSplitLegResponse(SplitLegResponse splitLegResponse) {

    }

    @Override
    public void onCallGapRequest(CallGapRequest callGapRequest) {

    }

    @Override
    public void onErrorComponent(CAPDialog capDialog, Long aLong, CAPErrorMessage capErrorMessage) {

    }

    @Override
    public void onRejectComponent(CAPDialog capDialog, Long aLong, Problem problem, boolean b) {

    }

    @Override
    public void onInvokeTimeout(CAPDialog capDialog, Long aLong) {

    }

    @Override
    public void onCAPMessage(CAPMessage capMessage) {

    }
}
