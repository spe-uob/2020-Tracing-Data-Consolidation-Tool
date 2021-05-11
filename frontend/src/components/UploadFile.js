import React from 'react';
import styles from './UploadFile.module.css';
import buttonStyles from './Button.module.css';
import { backendBaseUrl } from '../config';

class UploadFile extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
			file: '',
			status: '',
			ConsolidateError:''
		};
	}

	onFileChange = (event) => {
		this.setState({
			file: event.target.files[0],
		});
	}

	uploadFileData = (event) => {
		event.preventDefault();
		this.setState({status: ''});

		let data = new FormData();
		data.append('file', this.state.file);

		fetch(`${backendBaseUrl}/upload`, {
			method: 'POST',
			body: data
		}).then((response) => response.json()
		).then((jsonData)=> {
			console.log(jsonData.jobId); // DEBUG
			console.log(jsonData.error)
			this.props.markUploaded(jsonData.jobId);
			if (jsonData.error === ""){
				this.setState({
					status:"File successfully Consolidated",
		    });
		    }
			else{
				this.setState({
					status: "File sucessfully uploaded but an error has occurred during the conlidation process",
					ConsolidateError: jsonData.error
				})
			}
			
		}).catch(err => {
			console.log(err); // DEBUG
			this.setState({ status: "File failed to upload" });
		});

	}

	render() {
		return (
			<div className={styles.main}>
				<h1 className={styles.header}>Excel Files to Upload</h1>
				<h3 className={styles.header}>Upload a File</h3>
				<input onChange={this.onFileChange} type="file" />
				<button className={buttonStyles.button} disabled={!this.state.file} onClick={this.uploadFileData}>Upload</button>
				<h4 className={styles.statusMessage}>{this.state.status}</h4>
				<h4 className={styles.statusMessage}>{this.state.ConsolidateError}</h4>
			</div>
		)
	}
}

export default UploadFile;
