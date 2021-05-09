import React from 'react';
import styles from './DownloadFile.module.css';
import buttonStyles from './Button.module.css';
import ProgressBar from './ProgressBar/ProgressBar';
import { backendBaseUrl } from '../config';

class DownloadFile extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			showbar: false, // TODO
			eventSource: null,
			status: '',
		};
	}

	componentDidMount() {
		this.startCheckingProgress();
	}

	componentDidUpdate() {
		this.startCheckingProgress();
	}

	startCheckingProgress() {
		// if job submitted but not complete, and not already checking, start checking
		if (this.props.jobId && !this.props.fileProcessed && !this.state.eventSource) {
			let eventSource = new EventSource(`${backendBaseUrl}/progress?jobId=${this.props.jobId}`);
			eventSource.onmessage = event => this.updateProgress(JSON.parse(event.data));
			this.setState({eventSource});
		}
	}

	updateProgress(data) {
		if (typeof data !== "boolean") console.error("progress update is not boolean");

		if (data) {
			this.props.markFileProcessed();
			this.state.eventSource.close(); // TODO how upload second file
			this.setState({ eventSource: null });
		}
	}

	// TODO
	onProgressComplete = () => {
		this.setState({status: "Successfully consolidated", showbar: false});
		// TODO onFailure as well?
	}

	render() {
		const {showbar} = this.state;
		const processedUrl = `${backendBaseUrl}/processed?jobId=${this.props.jobId}`;

		return (
			<div className={styles.main}>
				<h1 className={styles.header}>Download Processed Files</h1>
				<div className={styles.note}>Please note that processing may take up to 30 seconds.</div>
				<div className={styles.buttonContainer}>
					<div />
					<a className={`${buttonStyles.button} ${this.props.fileProcessed ? '' : buttonStyles.inactive}`}
						href={this.props.fileProcessed ? processedUrl : null}>Download</a>
				</div>
				{showbar ? <ProgressBar onComplete={this.onProgressComplete}></ProgressBar> : null}
				<h4 className={styles.statusMessage}>{this.state.status}</h4>
			</div>
		);
	}
}

export default DownloadFile;
