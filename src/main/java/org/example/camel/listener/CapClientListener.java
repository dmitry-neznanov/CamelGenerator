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
import org.example.camel.generator.CapRequestTracker;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

public class CapClientListener implements
        CAPServiceCircuitSwitchedCallListener,
        CAPDialogListener {

    private final CapRequestTracker requestTracker;

    public CapClientListener(CapRequestTracker requestTracker) {
        this.requestTracker = Objects.requireNonNull(requestTracker);
    }

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
    public void onDialogUserAbort(
            CAPDialog dialog,
            CAPGeneralAbortReason generalReason,
            CAPUserAbortReason userReason) {

        requestTracker.fail(
                dialog.getLocalDialogId(),
                new IllegalStateException(
                        "CAP User Abort. Dialog ID="
                                + dialog.getLocalDialogId()
                                + ", reason=" + userReason
                )
        );
    }

    @Override
    public void onDialogProviderAbort(
            CAPDialog dialog,
            PAbortCauseType cause) {

        requestTracker.fail(
                dialog.getLocalDialogId(),
                new IllegalStateException(
                        "CAP Provider Abort. Dialog ID="
                                + dialog.getLocalDialogId()
                                + ", cause=" + cause
                )
        );
    }

    @Override
    public void onDialogClose(CAPDialog capDialog) {

    }

    @Override
    public void onDialogRelease(CAPDialog dialog) {

        long dialogId = dialog.getLocalDialogId();

        System.out.println(
                "CAP dialog released. Dialog ID=" + dialogId
        );

        requestTracker.fail(
                dialogId,
                new IllegalStateException(
                        "CAP dialog released without Continue. Dialog ID="
                                + dialogId
                )
        );
    }

    @Override
    public void onDialogTimeout(CAPDialog dialog) {

        long dialogId = dialog.getLocalDialogId();

        System.err.println(
                "CAP dialog timeout. Dialog ID=" + dialogId
        );

        requestTracker.fail(
                dialogId,
                new TimeoutException(
                        "CAP dialog timeout: " + dialogId
                )
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

        long dialogId = dialog.getLocalDialogId();

        System.out.println(
                "CAP Continue received. Dialog ID=" + dialogId
        );

        requestTracker.complete(dialogId);
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
    public void onErrorComponent(
            CAPDialog dialog,
            Long invokeId,
            CAPErrorMessage error) {

        requestTracker.fail(
                dialog.getLocalDialogId(),
                new IllegalStateException(
                        "CAP component error. Dialog ID="
                                + dialog.getLocalDialogId()
                                + ", invokeId=" + invokeId
                                + ", error=" + error
                )
        );
    }

    @Override
    public void onRejectComponent(
            CAPDialog dialog,
            Long invokeId,
            Problem problem,
            boolean isLocalOriginated) {

        requestTracker.fail(
                dialog.getLocalDialogId(),
                new IllegalStateException(
                        "CAP component rejected. Dialog ID="
                                + dialog.getLocalDialogId()
                                + ", invokeId=" + invokeId
                                + ", problem=" + problem
                )
        );
    }


    @Override
    public void onInvokeTimeout(
            CAPDialog dialog,
            Long invokeId) {

        requestTracker.fail(
                dialog.getLocalDialogId(),
                new TimeoutException(
                        "CAP invoke timeout. Dialog ID="
                                + dialog.getLocalDialogId()
                                + ", invokeId=" + invokeId
                )
        );
    }

    @Override
    public void onCAPMessage(CAPMessage capMessage) {

    }
}
