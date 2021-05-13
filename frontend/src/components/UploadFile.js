import React from 'react';
import styles from './UploadFile.module.css';
import { backendBaseUrl } from '../config';
import loadingGif from '../images/loading-small-clockwise.gif';
import { Upload as UploadIcon } from 'react-feather';

class UploadFile extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
			outbreakSource: '',
			file: '',
			statusMessage: '',
			error: false,
			loading: false,
			errorDetails: '', // error encountered during consolidation
		};
	}

	downloadblob(blob, filename) {
		const url = URL.createObjectURL(blob);
		// Create a new anchor element
		const a = document.createElement('a');
		// Set the href and download attributes for the anchor element
		// You can optionally set other attributes like `title`, etc
		// Especially, if the anchor element will be attached to the DOM
		a.href = url;
		a.download = filename || 'download';
		// Click handler that releases the object URL after the element has been clicked
		// This is required for one-off downloads of the blob content
		const clickHandler = () => {
			setTimeout(() => {
				URL.revokeObjectURL(url);
				a.removeEventListener('click', clickHandler);
			}, 150);
		};
		// Add the click event listener on the anchor element
		// Comment out this line if you don't want a one-off download of the blob content
		a.addEventListener('click', clickHandler, false);
		// Programmatically trigger a click on the anchor element
		// Useful if you want the download to happen automatically
		// Without attaching the anchor element to the DOM
		// Comment out this line if you don't want an automatic download of the blob content
		a.click();
		// Return the anchor element
		// Useful if you want a reference to the element
		// in order to attach it to the DOM or use it in some other way
		return a;
	}

	onSuccessfulConsolidation (jobId) {
		fetch(`${backendBaseUrl}/processed?jobId=${jobId}`, {
			method: 'GET'
		}).then(response => response.blob())
		.then(blob => {
			this.downloadblob(blob, "processed.xlsx");
		}).catch(error => console.log(error));
	}

	onFileChange = event => {
		this.setState({
			file: event.target.files[0],
		});
	}

	onValueChange = event => {
		this.setState({
			outbreakSource: event.target.value
		});
	}

	uploadFileData = event => {
		event.preventDefault();

		this.setState({
			statusMessage: 'Please wait while data is being processed',
			errorDetails: '',
			error: false,
			loading: true,
		});

		let requestBody = new FormData();
		requestBody.append('file', this.state.file);
		requestBody.append('outbreakSource', this.state.outbreakSource);

		fetch(`${backendBaseUrl}/upload`, {
			method: 'POST',
			body: requestBody
		}).then(response => response.json()).then(data => {
			console.log(data.error) // DEBUG
			this.props.markUploaded(data.jobId);
			if (data.error === "") {
				this.onSuccessfulConsolidation(data.jobId)
				this.setState({
					statusMessage: 'File successfully consolidated',
					errorDetails: '',
					error: false,
					loading: false,
				});
			} else {
				this.setState({
					statusMessage: 'File sucessfully uploaded but encountered error during consolidation',
					errorDetails: data.error,
					error: true,
					loading: false,
				});
			}
		}).catch(err => {
			console.log(err); // DEBUG
			this.setState({
				statusMessage: 'File failed to upload',
				errorDetails: '',
				error: true,
				loading: false,
			});
		});
	}

	render() {
		const canUpload = this.state.file && /^[0-9]{2}\/[0-9]{3}\/[0-9]{4}$/.test(this.state.outbreakSource);

		return (
			<div>
				<div className={styles.uploadContainer}>
					<table>
						<tbody>
							<tr>
								<td><div className={styles.header}>Outbreak Source (CPH):</div></td>
								<td className={styles.inputContainer}>
									<input onChange={this.onValueChange} type="value" placeholder="00/000/0000" />
								</td>
							</tr>
							<tr>
								<td><div className={styles.header}>Excel File:</div></td>
								<td className={styles.inputContainer}>
									<input onChange={this.onFileChange} type="file" />
								</td>
							</tr>
						</tbody>
					</table>
					<button className={`${styles.button} ${!canUpload ? styles.inactive : ''}`}
						title="Upload" disabled={!canUpload} onClick={this.uploadFileData}>

						<UploadIcon className={styles.uploadIcon} />
					</button>
				</div>
				<div className={styles.progressContainer}>
					<div className={`${styles.statusMessage} ${this.state.error ? styles.error : ''}`}>{this.state.statusMessage}</div>
					<div className={styles.progressImageContainer}>
						{this.state.loading ? <img className={styles.loadingImage} src={loadingGif} alt="loading..." /> : null}
					</div>
				</div>
				<div className={`${styles.details} ${styles.error}`}>{this.state.errorDetails}</div>
			</div>
		);
	}
}

export default UploadFile;
