import React from 'react';
import styles from './UploadFile.module.css';
import buttonStyles from './Button.module.css';
import { backendBaseUrl } from '../config';
import loadinggif from '../images/Spinner-1s-200px.gif';

class UploadFile extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
			OutbreakSource: '',
			file: '',
			status: '',
			ConsolidateError:'',
			loading: false,
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

	onFileChange = (event) => {
		this.setState({
			file: event.target.files[0],
		});
	}

	onValueChange = (event) => {
		this.setState({
			outbreakSource: event.target.value
		});
	}

	uploadFileData = (event) => {
		event.preventDefault();
		this.setState({
			status: 'Please wait while data is being processed',
			loading: true,
		});

		let data = new FormData();
		data.append('file', this.state.file);
		data.append('OutbreakSource', this.state.outbreakSource)

		fetch(`${backendBaseUrl}/upload`, {
			method: 'POST',
			body: data
		}).then(response => response.json()).then(jsonData => {
			console.log(jsonData.jobId); // DEBUG
			console.log(jsonData.error)
			this.props.markUploaded(jsonData.jobId);
			if (jsonData.error === "") {
				this.onSuccessfulConsolidation(jsonData.jobId)
				this.setState({
					status:"File successfully Consolidated",
					loading: false,
				});
			} else {
				this.setState({
					status: "File sucessfully uploaded but an error has occurred during consolidation",
					//ConsolidateError: jsonData.error,
					loading: false,
				});
			}
		}).catch(err => {
			console.log(err); // DEBUG
			this.setState({
				status: "File failed to upload" ,
				loading: false,
			});
		});
	}

	render() {
		const {loading} = this.state;
		return (
			<div className={styles.main}>
				<h1 className={styles.header}>Files to Consolidate</h1>
				<table>
					<tr>
						<td><div className={styles.header}>Outbreak Source (CPH):</div></td>
						<td className={styles.inputContainer}><input onChange={this.onValueChange} type="value" /></td>
					</tr>
					<tr>
						<td><div className={styles.header}>Excel File:</div></td>
						<td className={styles.inputContainer}><input onChange={this.onFileChange} type="file" /></td>
					</tr>
				</table>
				<button className={buttonStyles.button} disabled={!this.state.file} onClick={this.uploadFileData}>Upload</button>
				<h4 className={styles.statusMessage}>{this.state.status}</h4>
				<div className={styles.loadingImageContainer}>{loading ? <img className={styles.loadingImage} src={loadinggif}/> : null}</div>
				<h4 className={styles.statusMessage}>{this.state.ConsolidateError}</h4>
			</div>
		);
	}
}

export default UploadFile;
